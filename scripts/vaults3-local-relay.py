#!/usr/bin/env python3
"""Local-only development relay for environments that route Java S3 traffic through a broken proxy.

Usage: python3 scripts/vaults3-local-relay.py --upstream http://YOUR_VAULTS3_HOST:9000
Then start the backend with NNM_VAULTS3_ENDPOINT=http://127.0.0.1:19000.
The relay forwards raw bytes, including S3 signatures, without storing credentials.
"""

import argparse
import socket
import sys
import threading
from urllib.parse import urlsplit

LISTEN_HOST = "127.0.0.1"
MAX_CONNECTIONS = 4


def forward(source: socket.socket, destination: socket.socket) -> None:
    try:
        while data := source.recv(65536):
            destination.sendall(data)
    except OSError:
        pass
    finally:
        try:
            destination.shutdown(socket.SHUT_WR)
        except OSError:
            pass


def serve_connection(client: socket.socket, upstream: tuple[str, int], slots: threading.BoundedSemaphore) -> None:
    try:
        with client, socket.create_connection(upstream, timeout=10) as remote:
            remote.settimeout(None)  # 连接超时不应限制大文件传输或 S3 的较长响应。
            sender = threading.Thread(target=forward, args=(client, remote), daemon=True)
            sender.start()
            forward(remote, client)
            try:
                client.shutdown(socket.SHUT_RD)
            except OSError:
                pass
            sender.join()
    except OSError as error:
        print(f"VaultS3 relay connection failed: {error}", file=sys.stderr)
    finally:
        slots.release()


def main() -> None:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--upstream", required=True, help="The existing HTTP VaultS3 origin")
    parser.add_argument("--port", type=int, default=19000, help="Loopback listen port (default: 19000)")
    args = parser.parse_args()
    origin = urlsplit(args.upstream)
    if origin.scheme != "http" or not origin.hostname or origin.username or origin.password or origin.path not in ("", "/") or origin.query or origin.fragment:
        parser.error("--upstream must be an HTTP origin without credentials, path or query")
    try:
        upstream = (origin.hostname, origin.port or 80)
    except ValueError as error:
        parser.error(str(error))
    if not 1 <= args.port <= 65535 or (upstream[0] in {LISTEN_HOST, "localhost", "::1"} and upstream[1] == args.port):
        parser.error("--port must be valid and the relay must not forward to itself")

    slots = threading.BoundedSemaphore(MAX_CONNECTIONS)
    with socket.create_server((LISTEN_HOST, args.port), backlog=MAX_CONNECTIONS) as listener:
        print(f"VaultS3 relay listening on http://{LISTEN_HOST}:{args.port} -> {args.upstream}", flush=True)
        try:
            while True:
                client, _ = listener.accept()
                if not slots.acquire(blocking=False):
                    client.close()
                    continue
                threading.Thread(target=serve_connection, args=(client, upstream, slots), daemon=True).start()
        except KeyboardInterrupt:
            pass


if __name__ == "__main__":
    main()

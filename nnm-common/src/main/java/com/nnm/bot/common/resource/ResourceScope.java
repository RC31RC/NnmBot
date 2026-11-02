package com.nnm.bot.common.resource;

import com.nnm.bot.common.constant.CommonErrorCode;
import com.nnm.bot.common.exception.SystemException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Collections;

/** 单次同步调用拥有的资源集合，按登记的逆序关闭；不传播到异步工作线程。 */
public final class ResourceScope implements AutoCloseable {

    /** 资源按身份去重，防止同一附件在结果变换后被重复登记。 */
    private final Set<AutoCloseable> identities = Collections.newSetFromMap(new IdentityHashMap<>());
    /** 登记顺序决定释放顺序，后创建的依赖先关闭。 */
    private final List<AutoCloseable> resources = new ArrayList<>();
    /** 关闭后不再接受新资源，重复关闭无副作用。 */
    private boolean closed;

    /** 登记当前调用取得所有权的资源，并返回原资源。 */
    public <T extends AutoCloseable> T add(T resource) {
        Objects.requireNonNull(resource, "资源不能为空");
        if (closed)
            throw new IllegalStateException("资源作用域已经关闭");
        if (identities.add(resource))
            resources.add(resource);
        return resource;
    }

    /** 登记同一调用返回的多个资源。 */
    public void addAll(Collection<? extends AutoCloseable> values) {
        values.forEach(this::add);
    }

    /**
     * 关闭全部资源并交回可报告的清理异常；一个失败不阻止其余资源释放。 Error 在完成其余清理后原样传播，不转换为业务失败。
     *
     * @return 按关闭顺序收集的异常，空列表表示清理成功
     */
    public List<Exception> closeFailures() {
        if (closed)
            return List.of();
        closed = true;
        List<Exception> failures = new ArrayList<>();
        Error fatal = null;
        for (int index = resources.size() - 1; index >= 0; index--) {
            try {
                resources.get(index).close();
            } catch (Exception failure) {
                failures.add(failure);
            } catch (Error failure) {
                if (fatal == null)
                    fatal = failure;
                else if (fatal != failure)
                    fatal.addSuppressed(failure);
            }
        }
        resources.clear();
        identities.clear();
        if (fatal != null) {
            failures.forEach(fatal::addSuppressed);
            throw fatal;
        }
        return List.copyOf(failures);
    }

    /** 释放资源；与 try-with-resources 配合时，清理失败作为原异常的 suppressed 保留。 */
    @Override
    public void close() {
        List<Exception> failures = closeFailures();
        if (failures.isEmpty())
            return;
        RuntimeException exception =
            failures.getFirst() instanceof RuntimeException runtime ? runtime : new SystemException(
                CommonErrorCode.RESOURCE_CLEANUP_FAILED, "调用资源清理失败，共 " + failures.size() + " 项", failures.getFirst());
        failures.stream().skip(1).filter(value -> value != exception).forEach(exception::addSuppressed);
        throw exception;
    }
}

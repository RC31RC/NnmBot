--
-- PostgreSQL database dump
--

\restrict ra9eszdS9qXfHaTARXT2MkQ3PB9YOMIgOGjNz3wUYbIkbjKOzb9wsgkaTzDsyFu

-- Dumped from database version 18.4
-- Dumped by pg_dump version 18.4

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: nnmbot; Type: SCHEMA; Schema: -; Owner: -
--

CREATE SCHEMA nnmbot;


SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- Name: alias; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.alias (
    id bigint NOT NULL,
    cn_name character varying(200) NOT NULL,
    en_name character varying(200) NOT NULL,
    alias_name character varying(200) NOT NULL
);


--
-- Name: TABLE alias; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.alias IS 'Warframe 数据别名';


--
-- Name: COLUMN alias.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.alias.id IS '别名主键';


--
-- Name: COLUMN alias.cn_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.alias.cn_name IS '中文名称';


--
-- Name: COLUMN alias.en_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.alias.en_name IS '英文名称';


--
-- Name: COLUMN alias.alias_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.alias.alias_name IS '别名';


--
-- Name: alias_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.alias_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: alias_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.alias_id_seq OWNED BY nnmbot.alias.id;


--
-- Name: bot_admin; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_admin (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    bot_id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: TABLE bot_admin; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_admin IS '用户与机器人实例的管理员关联';


--
-- Name: COLUMN bot_admin.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_admin.id IS '管理员关联主键';


--
-- Name: COLUMN bot_admin.user_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_admin.user_id IS '用户主键';


--
-- Name: COLUMN bot_admin.bot_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_admin.bot_id IS '机器人主键';


--
-- Name: COLUMN bot_admin.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_admin.created_at IS '创建时间';


--
-- Name: bot_admin_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_admin_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_admin_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_admin_id_seq OWNED BY nnmbot.bot_admin.id;


--
-- Name: bot_channel_instance; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_channel_instance (
    id bigint NOT NULL,
    platform character varying(32) NOT NULL,
    instance_id character varying(100) NOT NULL,
    display_name character varying(100) NOT NULL,
    bot_account_id character varying(100) NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_bot_channel_instance_platform CHECK ((((platform)::text = 'ONEBOT'::text) OR ((platform)::text = 'QQ_OFFICIAL'::text)))
);


--
-- Name: TABLE bot_channel_instance; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_channel_instance IS '机器人渠道实例及其运行开关';


--
-- Name: COLUMN bot_channel_instance.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_channel_instance.id IS '渠道实例主键';


--
-- Name: COLUMN bot_channel_instance.platform; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_channel_instance.platform IS '渠道平台类型';


--
-- Name: COLUMN bot_channel_instance.instance_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_channel_instance.instance_id IS '渠道实例业务标识';


--
-- Name: COLUMN bot_channel_instance.display_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_channel_instance.display_name IS '渠道实例显示名称';


--
-- Name: COLUMN bot_channel_instance.bot_account_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_channel_instance.bot_account_id IS '机器人账号标识';


--
-- Name: COLUMN bot_channel_instance.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_channel_instance.enabled IS '是否启用';


--
-- Name: COLUMN bot_channel_instance.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_channel_instance.created_at IS '创建时间';


--
-- Name: COLUMN bot_channel_instance.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_channel_instance.updated_at IS '最后更新时间';


--
-- Name: bot_channel_instance_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_channel_instance_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_channel_instance_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_channel_instance_id_seq OWNED BY nnmbot.bot_channel_instance.id;


--
-- Name: bot_command; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_command (
    id bigint NOT NULL,
    code character varying(80) NOT NULL,
    name character varying(100) NOT NULL,
    game_code character varying(40) DEFAULT 'system'::character varying NOT NULL,
    description character varying(255),
    enabled boolean DEFAULT true NOT NULL,
    show_in_help boolean DEFAULT true NOT NULL,
    output_mode character varying(20) DEFAULT 'TEXT'::character varying NOT NULL,
    permission_override character varying(100),
    group_role_required character varying(20),
    cooldown_seconds bigint DEFAULT 0 NOT NULL,
    mention_invoker boolean DEFAULT false NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_bot_command_cooldown CHECK ((cooldown_seconds >= 0)),
    CONSTRAINT ck_bot_command_group_role CHECK (((group_role_required IS NULL) OR ((group_role_required)::text = ANY (ARRAY[('OWNER'::character varying)::text, ('ADMIN'::character varying)::text])))),
    CONSTRAINT ck_bot_command_output_mode CHECK (((output_mode)::text = ANY (ARRAY[('TEXT'::character varying)::text, ('IMAGE'::character varying)::text, ('FILE'::character varying)::text, ('MARKDOWN'::character varying)::text])))
);


--
-- Name: TABLE bot_command; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_command IS '机器人命令的可变管理配置';


--
-- Name: COLUMN bot_command.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command.id IS '命令配置主键';


--
-- Name: COLUMN bot_command.code; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command.code IS '与 Java 插件稳定对应的命令编码';


--
-- Name: COLUMN bot_command.name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command.name IS '命令显示名称';


--
-- Name: COLUMN bot_command.game_code; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command.game_code IS '命令所属游戏或业务域';


--
-- Name: COLUMN bot_command.description; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command.description IS '命令说明';


--
-- Name: COLUMN bot_command.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command.enabled IS '是否启用';


--
-- Name: COLUMN bot_command.show_in_help; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command.show_in_help IS '是否显示在帮助菜单';


--
-- Name: COLUMN bot_command.output_mode; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command.output_mode IS '命令默认数据载体：TEXT、IMAGE、FILE、MARKDOWN；CommandResult.text 摘要始终发送';


--
-- Name: COLUMN bot_command.permission_override; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command.permission_override IS '覆盖插件默认权限码';


--
-- Name: COLUMN bot_command.group_role_required; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command.group_role_required IS '群组角色要求：OWNER、ADMIN；为空表示不限制群组角色';


--
-- Name: COLUMN bot_command.cooldown_seconds; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command.cooldown_seconds IS '冷却时间，单位：秒';


--
-- Name: COLUMN bot_command.mention_invoker; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command.mention_invoker IS '是否在支持提及能力的渠道回复中提及命令触发者';


--
-- Name: COLUMN bot_command.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command.created_at IS '创建时间';


--
-- Name: COLUMN bot_command.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command.updated_at IS '最后更新时间';


--
-- Name: bot_command_alias; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_command_alias (
    id bigint NOT NULL,
    command_id bigint NOT NULL,
    alias_name character varying(100) NOT NULL,
    boundary_policy character varying(30) DEFAULT 'WHITESPACE_REQUIRED'::character varying NOT NULL,
    CONSTRAINT ck_bot_command_alias_boundary_policy CHECK (((boundary_policy)::text = ANY (ARRAY[('WHITESPACE_REQUIRED'::character varying)::text, ('CJK_OR_WHITESPACE'::character varying)::text, ('NONE'::character varying)::text])))
);


--
-- Name: TABLE bot_command_alias; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_command_alias IS '机器人命令自定义别名';


--
-- Name: COLUMN bot_command_alias.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_alias.id IS '命令别名主键';


--
-- Name: COLUMN bot_command_alias.command_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_alias.command_id IS '命令配置主键';


--
-- Name: COLUMN bot_command_alias.alias_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_alias.alias_name IS '命令自定义别名';


--
-- Name: COLUMN bot_command_alias.boundary_policy; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_alias.boundary_policy IS '前缀命令参数边界策略：必须空格、CJK或空格、无限制';


--
-- Name: bot_command_alias_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_command_alias_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_command_alias_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_command_alias_id_seq OWNED BY nnmbot.bot_command_alias.id;


--
-- Name: bot_command_channel; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_command_channel (
    id bigint NOT NULL,
    command_id bigint NOT NULL,
    platform character varying(32) NOT NULL,
    output_mode character varying(20) NOT NULL,
    CONSTRAINT ck_bot_command_channel_output_mode CHECK (((output_mode)::text = ANY (ARRAY[('NONE'::character varying)::text, ('TEXT'::character varying)::text, ('IMAGE'::character varying)::text, ('FILE'::character varying)::text, ('MARKDOWN'::character varying)::text])))
);


--
-- Name: TABLE bot_command_channel; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_command_channel IS '命令按渠道定义的输出配置';


--
-- Name: COLUMN bot_command_channel.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_channel.id IS '命令渠道输出配置主键';


--
-- Name: COLUMN bot_command_channel.command_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_channel.command_id IS '命令配置主键';


--
-- Name: COLUMN bot_command_channel.platform; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_channel.platform IS '渠道编码，如 ONEBOT、QQ_OFFICIAL、KOOK';


--
-- Name: COLUMN bot_command_channel.output_mode; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_channel.output_mode IS '渠道数据载体：NONE、TEXT、IMAGE、FILE、MARKDOWN；NONE 仅发送摘要，不发送 data 或附件';


--
-- Name: bot_command_channel_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_command_channel_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_command_channel_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_command_channel_id_seq OWNED BY nnmbot.bot_command_channel.id;


--
-- Name: bot_command_cost; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_command_cost (
    id bigint NOT NULL,
    command_id bigint NOT NULL,
    cost_points bigint DEFAULT 0 NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    reward_points bigint DEFAULT 0 NOT NULL,
    reward_enabled boolean DEFAULT false NOT NULL,
    description character varying(255),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_bot_command_cost_points CHECK ((cost_points >= 0)),
    CONSTRAINT ck_bot_command_reward_points CHECK ((reward_points >= 0))
);


--
-- Name: TABLE bot_command_cost; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_command_cost IS '命令在逻辑群组中的积分扣除与成功奖励规则';


--
-- Name: COLUMN bot_command_cost.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_cost.id IS '命令积分规则主键';


--
-- Name: COLUMN bot_command_cost.command_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_cost.command_id IS '命令配置主键，一个命令最多一条积分规则';


--
-- Name: COLUMN bot_command_cost.cost_points; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_cost.cost_points IS '单次执行消耗的积分数量，零表示不消耗';


--
-- Name: COLUMN bot_command_cost.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_cost.enabled IS '是否启用该命令的积分检查';


--
-- Name: COLUMN bot_command_cost.reward_points; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_cost.reward_points IS '命令业务和响应均成功后增加的积分数量';


--
-- Name: COLUMN bot_command_cost.reward_enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_cost.reward_enabled IS '是否启用命令成功奖励';


--
-- Name: COLUMN bot_command_cost.description; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_cost.description IS '积分扣除或奖励规则说明';


--
-- Name: COLUMN bot_command_cost.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_cost.created_at IS '创建时间';


--
-- Name: COLUMN bot_command_cost.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_cost.updated_at IS '最后更新时间';


--
-- Name: bot_command_cost_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_command_cost_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_command_cost_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_command_cost_id_seq OWNED BY nnmbot.bot_command_cost.id;


--
-- Name: bot_command_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_command_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_command_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_command_id_seq OWNED BY nnmbot.bot_command.id;


--
-- Name: bot_command_robot_role; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_command_robot_role (
    id bigint NOT NULL,
    command_id bigint NOT NULL,
    robot_role_id bigint NOT NULL
);


--
-- Name: TABLE bot_command_robot_role; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_command_robot_role IS '命令与机器人能力角色关联';


--
-- Name: COLUMN bot_command_robot_role.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_robot_role.id IS '命令与机器人角色关联主键';


--
-- Name: COLUMN bot_command_robot_role.command_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_robot_role.command_id IS '命令配置主键';


--
-- Name: COLUMN bot_command_robot_role.robot_role_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_robot_role.robot_role_id IS '机器人能力角色主键';


--
-- Name: bot_command_robot_role_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_command_robot_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_command_robot_role_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_command_robot_role_id_seq OWNED BY nnmbot.bot_command_robot_role.id;


--
-- Name: bot_command_role; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_command_role (
    id bigint NOT NULL,
    command_id bigint NOT NULL,
    role_id bigint NOT NULL
);


--
-- Name: TABLE bot_command_role; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_command_role IS '命令与系统角色的使用范围关联';


--
-- Name: COLUMN bot_command_role.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_role.id IS '命令与系统角色关联主键';


--
-- Name: COLUMN bot_command_role.command_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_role.command_id IS '命令配置主键';


--
-- Name: COLUMN bot_command_role.role_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_role.role_id IS '系统角色主键';


--
-- Name: bot_command_role_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_command_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_command_role_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_command_role_id_seq OWNED BY nnmbot.bot_command_role.id;


--
-- Name: bot_command_template; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_command_template (
    id bigint NOT NULL,
    template_code character varying(80) NOT NULL,
    name character varying(100) NOT NULL,
    command_id bigint,
    sort_order integer DEFAULT 0 NOT NULL,
    html_content text NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: TABLE bot_command_template; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_command_template IS '机器人图片回复 HTML 模板';


--
-- Name: COLUMN bot_command_template.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_template.id IS 'HTML 模板主键';


--
-- Name: COLUMN bot_command_template.template_code; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_template.template_code IS '模板业务编码';


--
-- Name: COLUMN bot_command_template.name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_template.name IS '模板名称';


--
-- Name: COLUMN bot_command_template.command_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_template.command_id IS '所属命令主键，使用应用层逻辑关联，不建立数据库外键';


--
-- Name: COLUMN bot_command_template.sort_order; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_template.sort_order IS '同一命令下模板的默认渲染和展示顺序';


--
-- Name: COLUMN bot_command_template.html_content; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_template.html_content IS '模板 HTML 内容，变量由服务端白名单控制';


--
-- Name: COLUMN bot_command_template.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_template.enabled IS '是否启用';


--
-- Name: COLUMN bot_command_template.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_template.created_at IS '创建时间';


--
-- Name: COLUMN bot_command_template.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_command_template.updated_at IS '最后更新时间';


--
-- Name: bot_command_template_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_command_template_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_command_template_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_command_template_id_seq OWNED BY nnmbot.bot_command_template.id;


--
-- Name: bot_group; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_group (
    id bigint NOT NULL,
    code character varying(64) NOT NULL,
    name character varying(160) NOT NULL,
    description character varying(255),
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: TABLE bot_group; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_group IS '逻辑群组，作为跨渠道消息和业务状态的隔离主体';


--
-- Name: COLUMN bot_group.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group.id IS '逻辑群组主键';


--
-- Name: COLUMN bot_group.code; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group.code IS '逻辑群组业务编码';


--
-- Name: COLUMN bot_group.name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group.name IS '逻辑群组名称';


--
-- Name: COLUMN bot_group.description; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group.description IS '逻辑群组说明';


--
-- Name: COLUMN bot_group.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group.enabled IS '是否启用';


--
-- Name: COLUMN bot_group.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group.created_at IS '创建时间';


--
-- Name: COLUMN bot_group.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group.updated_at IS '最后更新时间';


--
-- Name: bot_group_bot; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_group_bot (
    id bigint NOT NULL,
    group_id bigint NOT NULL,
    bot_instance_id bigint NOT NULL,
    platform character varying(32) NOT NULL,
    scope_type character varying(20) DEFAULT 'GROUP'::character varying NOT NULL,
    scope_key character varying(160) NOT NULL,
    display_name character varying(160),
    enabled boolean DEFAULT true NOT NULL
);


--
-- Name: TABLE bot_group_bot; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_group_bot IS '逻辑群组与渠道机器人实例及平台侧群组标识的关联';


--
-- Name: COLUMN bot_group_bot.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_bot.id IS '群组机器人绑定主键';


--
-- Name: COLUMN bot_group_bot.group_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_bot.group_id IS '逻辑群组主键';


--
-- Name: COLUMN bot_group_bot.bot_instance_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_bot.bot_instance_id IS '机器人实例主键';


--
-- Name: COLUMN bot_group_bot.platform; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_bot.platform IS '渠道平台，如 ONEBOT、QQ_OFFICIAL';


--
-- Name: COLUMN bot_group_bot.scope_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_bot.scope_type IS '平台作用域类型，如 GROUP、CHANNEL';


--
-- Name: COLUMN bot_group_bot.scope_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_bot.scope_key IS '平台侧群号、群 OpenID 或频道标识';


--
-- Name: COLUMN bot_group_bot.display_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_bot.display_name IS '平台侧群组显示名称';


--
-- Name: COLUMN bot_group_bot.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_bot.enabled IS '是否启用该机器人在当前逻辑群组中的服务';


--
-- Name: bot_group_bot_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_group_bot_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_group_bot_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_group_bot_id_seq OWNED BY nnmbot.bot_group_bot.id;


--
-- Name: bot_group_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_group_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_group_id_seq OWNED BY nnmbot.bot_group.id;


--
-- Name: bot_group_member; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_group_member (
    id bigint NOT NULL,
    group_id bigint NOT NULL,
    qq_id character varying(32),
    member_openid character varying(160),
    kook_id character varying(160),
    qq_id_verified boolean DEFAULT false NOT NULL,
    qq_bound_at timestamp without time zone,
    display_name character varying(160),
    member_role character varying(20) DEFAULT 'MEMBER'::character varying NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    points bigint DEFAULT 0 NOT NULL,
    total_earned bigint DEFAULT 0 NOT NULL,
    total_spent bigint DEFAULT 0 NOT NULL,
    version integer DEFAULT 0 NOT NULL,
    CONSTRAINT ck_bot_group_member_points CHECK (((points >= 0) AND (total_earned >= 0) AND (total_spent >= 0))),
    CONSTRAINT ck_bot_group_member_role CHECK (((member_role)::text = ANY (ARRAY[('OWNER'::character varying)::text, ('ADMIN'::character varying)::text, ('MEMBER'::character varying)::text])))
);


--
-- Name: TABLE bot_group_member; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_group_member IS '逻辑群组成员及其积分和平台身份';


--
-- Name: COLUMN bot_group_member.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_member.id IS '群组成员主键';


--
-- Name: COLUMN bot_group_member.group_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_member.group_id IS '逻辑群组主键';


--
-- Name: COLUMN bot_group_member.qq_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_member.qq_id IS 'OneBot 或绑定命令提供的 QQ 号';


--
-- Name: COLUMN bot_group_member.member_openid; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_member.member_openid IS 'QQ 官方机器人提供的群成员 OpenID';


--
-- Name: COLUMN bot_group_member.kook_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_member.kook_id IS 'KOOK 渠道提供的用户 ID';


--
-- Name: COLUMN bot_group_member.qq_id_verified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_member.qq_id_verified IS 'QQ 号是否通过 OneBot 等可信渠道核验';


--
-- Name: COLUMN bot_group_member.qq_bound_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_member.qq_bound_at IS '最近一次绑定 QQ 号的时间';


--
-- Name: COLUMN bot_group_member.display_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_member.display_name IS '渠道侧显示名称';


--
-- Name: COLUMN bot_group_member.member_role; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_member.member_role IS '平台群组角色：OWNER、ADMIN、MEMBER';


--
-- Name: COLUMN bot_group_member.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_member.enabled IS '是否启用该成员在逻辑群组中的身份';


--
-- Name: COLUMN bot_group_member.points; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_member.points IS '当前可用积分余额，数据库是最终权威值';


--
-- Name: COLUMN bot_group_member.total_earned; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_member.total_earned IS '累计获得积分';


--
-- Name: COLUMN bot_group_member.total_spent; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_member.total_spent IS '累计消耗积分';


--
-- Name: COLUMN bot_group_member.version; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_member.version IS '乐观锁版本号，避免并发签到或扣费覆盖余额';


--
-- Name: bot_group_member_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_group_member_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_group_member_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_group_member_id_seq OWNED BY nnmbot.bot_group_member.id;


--
-- Name: bot_group_point_transaction; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_group_point_transaction (
    id bigint NOT NULL,
    group_id bigint NOT NULL,
    member_id bigint NOT NULL,
    command_code character varying(80),
    transaction_type character varying(32) NOT NULL,
    amount bigint NOT NULL,
    balance_after bigint NOT NULL,
    idempotency_key character varying(160) NOT NULL,
    description character varying(255),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_bot_group_point_transaction_balance CHECK ((balance_after >= 0)),
    CONSTRAINT ck_bot_group_point_transaction_type CHECK (((transaction_type)::text = ANY (ARRAY[('SIGN_IN'::character varying)::text, ('COMMAND_COST'::character varying)::text, ('COMMAND_REWARD'::character varying)::text, ('ADMIN_ADJUST'::character varying)::text, ('REFUND'::character varying)::text])))
);


--
-- Name: TABLE bot_group_point_transaction; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_group_point_transaction IS '逻辑群组积分流水';


--
-- Name: COLUMN bot_group_point_transaction.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_point_transaction.id IS '积分流水主键';


--
-- Name: COLUMN bot_group_point_transaction.group_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_point_transaction.group_id IS '逻辑群组主键';


--
-- Name: COLUMN bot_group_point_transaction.member_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_point_transaction.member_id IS '群组成员主键';


--
-- Name: COLUMN bot_group_point_transaction.command_code; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_point_transaction.command_code IS '产生流水的命令编码，管理员调整时可以为空';


--
-- Name: COLUMN bot_group_point_transaction.transaction_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_point_transaction.transaction_type IS '流水类型：签到、命令消耗、命令成功奖励、管理员调整、失败退款';


--
-- Name: COLUMN bot_group_point_transaction.amount; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_point_transaction.amount IS '积分变化量，增加为正数，消耗为负数';


--
-- Name: COLUMN bot_group_point_transaction.balance_after; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_point_transaction.balance_after IS '写入流水后的成员余额快照';


--
-- Name: COLUMN bot_group_point_transaction.idempotency_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_point_transaction.idempotency_key IS '消息或管理操作幂等键，防止重复处理';


--
-- Name: COLUMN bot_group_point_transaction.description; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_point_transaction.description IS '流水说明';


--
-- Name: COLUMN bot_group_point_transaction.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_group_point_transaction.created_at IS '流水创建时间';


--
-- Name: bot_group_point_transaction_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_group_point_transaction_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_group_point_transaction_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_group_point_transaction_id_seq OWNED BY nnmbot.bot_group_point_transaction.id;


--
-- Name: bot_onebot_config; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_onebot_config (
    instance_id bigint NOT NULL,
    connection_url character varying(500) NOT NULL,
    access_token character varying(2000),
    reconnect_interval_seconds bigint DEFAULT 5 NOT NULL,
    CONSTRAINT ck_bot_onebot_reconnect_interval CHECK ((reconnect_interval_seconds > 0))
);


--
-- Name: TABLE bot_onebot_config; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_onebot_config IS 'OneBot 渠道实例连接配置';


--
-- Name: COLUMN bot_onebot_config.instance_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_onebot_config.instance_id IS '渠道实例主键，同时关联 bot_channel_instance.id';


--
-- Name: COLUMN bot_onebot_config.connection_url; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_onebot_config.connection_url IS 'OneBot 正向 WebSocket 连接地址';


--
-- Name: COLUMN bot_onebot_config.access_token; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_onebot_config.access_token IS 'OneBot 访问令牌密文';


--
-- Name: COLUMN bot_onebot_config.reconnect_interval_seconds; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_onebot_config.reconnect_interval_seconds IS 'WebSocket 重连间隔，单位：秒';


--
-- Name: bot_qq_official_config; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_qq_official_config (
    instance_id bigint NOT NULL,
    app_id character varying(200) NOT NULL,
    client_secret character varying(2000) NOT NULL,
    token_url character varying(500) NOT NULL,
    api_base_url character varying(500) NOT NULL,
    gateway_url character varying(500),
    intents bigint DEFAULT 117440512 NOT NULL,
    gateway_connect_timeout_seconds bigint DEFAULT 10 NOT NULL,
    reconnect_delay_seconds bigint DEFAULT 5 NOT NULL,
    CONSTRAINT ck_bot_qq_official_gateway_timeout CHECK ((gateway_connect_timeout_seconds > 0)),
    CONSTRAINT ck_bot_qq_official_intents CHECK ((intents >= 0)),
    CONSTRAINT ck_bot_qq_official_reconnect_delay CHECK ((reconnect_delay_seconds >= 0))
);


--
-- Name: TABLE bot_qq_official_config; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_qq_official_config IS 'QQ 官方机器人渠道连接配置';


--
-- Name: COLUMN bot_qq_official_config.instance_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_qq_official_config.instance_id IS '渠道实例主键，同时关联 bot_channel_instance.id';


--
-- Name: COLUMN bot_qq_official_config.app_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_qq_official_config.app_id IS 'QQ 官方机器人 AppID';


--
-- Name: COLUMN bot_qq_official_config.client_secret; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_qq_official_config.client_secret IS 'QQ 官方机器人 Client Secret 密文';


--
-- Name: COLUMN bot_qq_official_config.token_url; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_qq_official_config.token_url IS 'QQ 官方机器人访问令牌接口地址';


--
-- Name: COLUMN bot_qq_official_config.api_base_url; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_qq_official_config.api_base_url IS 'QQ 官方机器人接口基础地址';


--
-- Name: COLUMN bot_qq_official_config.gateway_url; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_qq_official_config.gateway_url IS 'QQ 官方机器人 Gateway 地址，为空时自动获取';


--
-- Name: COLUMN bot_qq_official_config.intents; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_qq_official_config.intents IS 'QQ 官方机器人订阅事件权限位';


--
-- Name: COLUMN bot_qq_official_config.gateway_connect_timeout_seconds; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_qq_official_config.gateway_connect_timeout_seconds IS 'Gateway 连接超时时间，单位：秒';


--
-- Name: COLUMN bot_qq_official_config.reconnect_delay_seconds; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_qq_official_config.reconnect_delay_seconds IS 'Gateway 重连延迟，单位：秒';


--
-- Name: bot_robot_role; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_robot_role (
    id bigint NOT NULL,
    code character varying(64) NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(255),
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: TABLE bot_robot_role; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_robot_role IS '机器人能力角色，不属于后台用户系统 RBAC';


--
-- Name: COLUMN bot_robot_role.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_robot_role.id IS '机器人能力角色主键';


--
-- Name: COLUMN bot_robot_role.code; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_robot_role.code IS '机器人能力角色业务编码';


--
-- Name: COLUMN bot_robot_role.name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_robot_role.name IS '机器人能力角色名称';


--
-- Name: COLUMN bot_robot_role.description; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_robot_role.description IS '机器人能力角色说明';


--
-- Name: COLUMN bot_robot_role.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_robot_role.enabled IS '是否启用';


--
-- Name: COLUMN bot_robot_role.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_robot_role.created_at IS '创建时间';


--
-- Name: COLUMN bot_robot_role.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_robot_role.updated_at IS '最后更新时间';


--
-- Name: bot_robot_role_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_robot_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_robot_role_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_robot_role_id_seq OWNED BY nnmbot.bot_robot_role.id;


--
-- Name: bot_robot_role_instance; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_robot_role_instance (
    id bigint NOT NULL,
    robot_role_id bigint NOT NULL,
    bot_instance_id bigint NOT NULL
);


--
-- Name: TABLE bot_robot_role_instance; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_robot_role_instance IS '机器人能力角色与机器人实例关联';


--
-- Name: COLUMN bot_robot_role_instance.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_robot_role_instance.id IS '机器人角色与实例关联主键';


--
-- Name: COLUMN bot_robot_role_instance.robot_role_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_robot_role_instance.robot_role_id IS '机器人能力角色主键';


--
-- Name: COLUMN bot_robot_role_instance.bot_instance_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_robot_role_instance.bot_instance_id IS '机器人渠道实例主键';


--
-- Name: bot_robot_role_instance_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_robot_role_instance_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_robot_role_instance_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_robot_role_instance_id_seq OWNED BY nnmbot.bot_robot_role_instance.id;


--
-- Name: bot_scope; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_scope (
    id bigint NOT NULL,
    platform character varying(32) NOT NULL,
    scope_type character varying(20) NOT NULL,
    scope_key character varying(160) NOT NULL,
    bot_instance_id bigint NOT NULL,
    display_name character varying(160),
    enabled boolean DEFAULT true NOT NULL
);


--
-- Name: TABLE bot_scope; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_scope IS '机器人在外部平台中的群组或频道作用域';


--
-- Name: COLUMN bot_scope.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_scope.id IS '机器人作用域主键';


--
-- Name: COLUMN bot_scope.platform; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_scope.platform IS '作用域所属渠道';


--
-- Name: COLUMN bot_scope.scope_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_scope.scope_type IS '作用域类型：GROUP、CHANNEL 等';


--
-- Name: COLUMN bot_scope.scope_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_scope.scope_key IS '渠道侧作用域标识，使用字符串兼容不同平台';


--
-- Name: COLUMN bot_scope.bot_instance_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_scope.bot_instance_id IS '所属机器人实例';


--
-- Name: COLUMN bot_scope.display_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_scope.display_name IS '作用域显示名称';


--
-- Name: COLUMN bot_scope.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_scope.enabled IS '是否启用';


--
-- Name: bot_scope_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_scope_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_scope_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_scope_id_seq OWNED BY nnmbot.bot_scope.id;


--
-- Name: bot_scope_member; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.bot_scope_member (
    id bigint NOT NULL,
    scope_id bigint NOT NULL,
    user_id bigint NOT NULL,
    platform_user_id character varying(160) NOT NULL,
    member_role character varying(20) DEFAULT 'MEMBER'::character varying NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    CONSTRAINT ck_bot_scope_member_role CHECK (((member_role)::text = ANY (ARRAY[('OWNER'::character varying)::text, ('ADMIN'::character varying)::text, ('MEMBER'::character varying)::text])))
);


--
-- Name: TABLE bot_scope_member; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.bot_scope_member IS '作用域成员及其作用域角色';


--
-- Name: COLUMN bot_scope_member.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_scope_member.id IS '作用域成员主键';


--
-- Name: COLUMN bot_scope_member.scope_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_scope_member.scope_id IS '机器人作用域主键';


--
-- Name: COLUMN bot_scope_member.user_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_scope_member.user_id IS '绑定的系统用户主键';


--
-- Name: COLUMN bot_scope_member.platform_user_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_scope_member.platform_user_id IS '渠道侧成员标识';


--
-- Name: COLUMN bot_scope_member.member_role; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_scope_member.member_role IS '作用域角色：OWNER、ADMIN、MEMBER';


--
-- Name: COLUMN bot_scope_member.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.bot_scope_member.enabled IS '是否启用';


--
-- Name: bot_scope_member_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.bot_scope_member_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: bot_scope_member_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.bot_scope_member_id_seq OWNED BY nnmbot.bot_scope_member.id;


--
-- Name: mission_subscribe; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.mission_subscribe (
    id bigint NOT NULL,
    group_id bigint NOT NULL,
    type character varying(50) NOT NULL,
    enabled boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: TABLE mission_subscribe; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.mission_subscribe IS '作用域任务订阅配置';


--
-- Name: COLUMN mission_subscribe.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.mission_subscribe.id IS '订阅主键';


--
-- Name: COLUMN mission_subscribe.group_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.mission_subscribe.group_id IS '群聊标识';


--
-- Name: COLUMN mission_subscribe.type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.mission_subscribe.type IS '订阅类型';


--
-- Name: COLUMN mission_subscribe.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.mission_subscribe.enabled IS '是否启用';


--
-- Name: COLUMN mission_subscribe.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.mission_subscribe.created_at IS '创建时间';


--
-- Name: mission_subscribe_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.mission_subscribe_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: mission_subscribe_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.mission_subscribe_id_seq OWNED BY nnmbot.mission_subscribe.id;


--
-- Name: notification_history; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.notification_history (
    id bigint NOT NULL,
    event_type character varying(50) NOT NULL,
    event_id character varying(100) NOT NULL,
    group_id bigint NOT NULL,
    sent_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: TABLE notification_history; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.notification_history IS '外部事件通知发送历史';


--
-- Name: COLUMN notification_history.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.notification_history.id IS '通知历史主键';


--
-- Name: COLUMN notification_history.event_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.notification_history.event_type IS '事件类型';


--
-- Name: COLUMN notification_history.event_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.notification_history.event_id IS '外部事件标识';


--
-- Name: COLUMN notification_history.group_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.notification_history.group_id IS '群聊标识';


--
-- Name: COLUMN notification_history.sent_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.notification_history.sent_at IS '发送时间';


--
-- Name: notification_history_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.notification_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: notification_history_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.notification_history_id_seq OWNED BY nnmbot.notification_history.id;


--
-- Name: sys_menu; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.sys_menu (
    id bigint NOT NULL,
    parent_id bigint,
    name character varying(100) NOT NULL,
    path character varying(255) NOT NULL,
    component character varying(255),
    title character varying(100) NOT NULL,
    icon character varying(100),
    sort_order integer DEFAULT 0 NOT NULL,
    keep_alive boolean DEFAULT false NOT NULL,
    visible boolean DEFAULT true NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    fixed_tab boolean DEFAULT false NOT NULL,
    hide_tab boolean DEFAULT false NOT NULL,
    full_page boolean DEFAULT false NOT NULL,
    iframe boolean DEFAULT false NOT NULL,
    link character varying(500),
    active_path character varying(255),
    text_badge character varying(50),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: TABLE sys_menu; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.sys_menu IS '管理端菜单与动态路由定义';


--
-- Name: COLUMN sys_menu.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.id IS '菜单主键';


--
-- Name: COLUMN sys_menu.parent_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.parent_id IS '父菜单主键';


--
-- Name: COLUMN sys_menu.name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.name IS '菜单唯一名称';


--
-- Name: COLUMN sys_menu.path; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.path IS '前端路由路径';


--
-- Name: COLUMN sys_menu.component; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.component IS '前端路由组件';


--
-- Name: COLUMN sys_menu.title; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.title IS '菜单标题';


--
-- Name: COLUMN sys_menu.icon; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.icon IS '菜单图标';


--
-- Name: COLUMN sys_menu.sort_order; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.sort_order IS '排序值';


--
-- Name: COLUMN sys_menu.keep_alive; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.keep_alive IS '是否缓存页面';


--
-- Name: COLUMN sys_menu.visible; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.visible IS '是否在菜单中可见';


--
-- Name: COLUMN sys_menu.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.enabled IS '是否启用';


--
-- Name: COLUMN sys_menu.fixed_tab; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.fixed_tab IS '是否固定标签页';


--
-- Name: COLUMN sys_menu.hide_tab; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.hide_tab IS '是否隐藏标签页';


--
-- Name: COLUMN sys_menu.full_page; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.full_page IS '是否使用整页布局';


--
-- Name: COLUMN sys_menu.iframe; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.iframe IS '是否以内嵌页面打开';


--
-- Name: COLUMN sys_menu.link; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.link IS '外部链接地址';


--
-- Name: COLUMN sys_menu.active_path; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.active_path IS '激活菜单路径';


--
-- Name: COLUMN sys_menu.text_badge; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.text_badge IS '菜单文字徽标';


--
-- Name: COLUMN sys_menu.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.created_at IS '创建时间';


--
-- Name: COLUMN sys_menu.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_menu.updated_at IS '最后更新时间';


--
-- Name: sys_menu_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.sys_menu_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: sys_menu_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.sys_menu_id_seq OWNED BY nnmbot.sys_menu.id;


--
-- Name: sys_operation_log; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.sys_operation_log (
    id bigint NOT NULL,
    operator_id bigint,
    operator_name character varying(50) NOT NULL,
    action character varying(80) NOT NULL,
    target_type character varying(50) NOT NULL,
    target_id character varying(100),
    detail character varying(1000),
    ip_address character varying(64),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: TABLE sys_operation_log; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.sys_operation_log IS '管理端操作审计日志';


--
-- Name: COLUMN sys_operation_log.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_operation_log.id IS '操作日志主键';


--
-- Name: COLUMN sys_operation_log.operator_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_operation_log.operator_id IS '操作人用户主键';


--
-- Name: COLUMN sys_operation_log.operator_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_operation_log.operator_name IS '操作人名称';


--
-- Name: COLUMN sys_operation_log.action; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_operation_log.action IS '操作动作编码';


--
-- Name: COLUMN sys_operation_log.target_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_operation_log.target_type IS '目标对象类型';


--
-- Name: COLUMN sys_operation_log.target_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_operation_log.target_id IS '目标对象标识';


--
-- Name: COLUMN sys_operation_log.detail; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_operation_log.detail IS '操作详情';


--
-- Name: COLUMN sys_operation_log.ip_address; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_operation_log.ip_address IS '操作人 IP 地址';


--
-- Name: COLUMN sys_operation_log.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_operation_log.created_at IS '操作时间';


--
-- Name: sys_operation_log_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.sys_operation_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: sys_operation_log_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.sys_operation_log_id_seq OWNED BY nnmbot.sys_operation_log.id;


--
-- Name: sys_permission; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.sys_permission (
    id bigint NOT NULL,
    code character varying(100) NOT NULL,
    name character varying(100) NOT NULL,
    type character varying(20) NOT NULL,
    menu_id bigint,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: TABLE sys_permission; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.sys_permission IS '管理端按钮和接口权限';


--
-- Name: COLUMN sys_permission.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_permission.id IS '权限主键';


--
-- Name: COLUMN sys_permission.code; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_permission.code IS '权限编码';


--
-- Name: COLUMN sys_permission.name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_permission.name IS '权限名称';


--
-- Name: COLUMN sys_permission.type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_permission.type IS '权限类型';


--
-- Name: COLUMN sys_permission.menu_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_permission.menu_id IS '所属菜单主键';


--
-- Name: COLUMN sys_permission.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_permission.enabled IS '是否启用';


--
-- Name: COLUMN sys_permission.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_permission.created_at IS '创建时间';


--
-- Name: COLUMN sys_permission.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_permission.updated_at IS '最后更新时间';


--
-- Name: sys_permission_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.sys_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: sys_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.sys_permission_id_seq OWNED BY nnmbot.sys_permission.id;


--
-- Name: sys_role; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.sys_role (
    id bigint NOT NULL,
    code character varying(50) NOT NULL,
    name character varying(100) NOT NULL,
    description character varying(255),
    enabled boolean DEFAULT true NOT NULL,
    authority_level integer DEFAULT 0 NOT NULL,
    data_scope character varying(20) DEFAULT 'SELF'::character varying NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: TABLE sys_role; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.sys_role IS '系统角色及其权限等级';


--
-- Name: COLUMN sys_role.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_role.id IS '角色主键';


--
-- Name: COLUMN sys_role.code; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_role.code IS '角色编码';


--
-- Name: COLUMN sys_role.name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_role.name IS '角色名称';


--
-- Name: COLUMN sys_role.description; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_role.description IS '角色描述';


--
-- Name: COLUMN sys_role.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_role.enabled IS '是否启用';


--
-- Name: COLUMN sys_role.authority_level; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_role.authority_level IS '角色权限等级，数值越大权限越高';


--
-- Name: COLUMN sys_role.data_scope; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_role.data_scope IS '数据范围';


--
-- Name: COLUMN sys_role.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_role.created_at IS '创建时间';


--
-- Name: COLUMN sys_role.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_role.updated_at IS '最后更新时间';


--
-- Name: sys_role_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.sys_role_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: sys_role_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.sys_role_id_seq OWNED BY nnmbot.sys_role.id;


--
-- Name: sys_role_menu; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.sys_role_menu (
    role_id bigint NOT NULL,
    menu_id bigint NOT NULL
);


--
-- Name: TABLE sys_role_menu; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.sys_role_menu IS '角色与菜单关联';


--
-- Name: COLUMN sys_role_menu.role_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_role_menu.role_id IS '角色主键';


--
-- Name: COLUMN sys_role_menu.menu_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_role_menu.menu_id IS '菜单主键';


--
-- Name: sys_role_permission; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.sys_role_permission (
    role_id bigint NOT NULL,
    permission_id bigint NOT NULL
);


--
-- Name: TABLE sys_role_permission; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.sys_role_permission IS '角色与权限关联';


--
-- Name: COLUMN sys_role_permission.role_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_role_permission.role_id IS '角色主键';


--
-- Name: COLUMN sys_role_permission.permission_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_role_permission.permission_id IS '权限主键';


--
-- Name: sys_user; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.sys_user (
    id bigint NOT NULL,
    username character varying(50) NOT NULL,
    password character varying(255) NOT NULL,
    nickname character varying(100),
    phone character varying(30),
    email character varying(120),
    gender character varying(20),
    avatar character varying(500),
    enabled boolean DEFAULT true,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


--
-- Name: TABLE sys_user; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.sys_user IS '管理端系统用户';


--
-- Name: COLUMN sys_user.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user.id IS '用户主键';


--
-- Name: COLUMN sys_user.username; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user.username IS '登录用户名';


--
-- Name: COLUMN sys_user.password; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user.password IS '密码哈希';


--
-- Name: COLUMN sys_user.nickname; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user.nickname IS '用户昵称';


--
-- Name: COLUMN sys_user.phone; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user.phone IS '手机号码';


--
-- Name: COLUMN sys_user.email; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user.email IS '电子邮箱';


--
-- Name: COLUMN sys_user.gender; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user.gender IS '性别';


--
-- Name: COLUMN sys_user.avatar; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user.avatar IS '头像地址';


--
-- Name: COLUMN sys_user.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user.enabled IS '是否启用';


--
-- Name: COLUMN sys_user.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user.created_at IS '创建时间';


--
-- Name: COLUMN sys_user.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user.updated_at IS '最后更新时间';


--
-- Name: sys_user_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.sys_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: sys_user_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.sys_user_id_seq OWNED BY nnmbot.sys_user.id;


--
-- Name: sys_user_identity; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.sys_user_identity (
    id bigint NOT NULL,
    user_id bigint NOT NULL,
    platform character varying(32) NOT NULL,
    external_user_id character varying(160) NOT NULL
);


--
-- Name: TABLE sys_user_identity; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.sys_user_identity IS '系统用户与外部平台身份绑定';


--
-- Name: COLUMN sys_user_identity.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user_identity.id IS '系统用户外部身份主键';


--
-- Name: COLUMN sys_user_identity.user_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user_identity.user_id IS '系统用户主键';


--
-- Name: COLUMN sys_user_identity.platform; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user_identity.platform IS '身份所属渠道，如 QQ、KOOK';


--
-- Name: COLUMN sys_user_identity.external_user_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user_identity.external_user_id IS '渠道侧用户标识';


--
-- Name: sys_user_identity_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.sys_user_identity_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: sys_user_identity_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.sys_user_identity_id_seq OWNED BY nnmbot.sys_user_identity.id;


--
-- Name: sys_user_role; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.sys_user_role (
    user_id bigint NOT NULL,
    role_id bigint NOT NULL
);


--
-- Name: TABLE sys_user_role; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.sys_user_role IS '用户与系统角色关联';


--
-- Name: COLUMN sys_user_role.user_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user_role.user_id IS '用户主键';


--
-- Name: COLUMN sys_user_role.role_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.sys_user_role.role_id IS '角色主键';


--
-- Name: wf_arbitration_schedule; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_arbitration_schedule (
    activation timestamp with time zone NOT NULL,
    expiry timestamp with time zone NOT NULL,
    upstream_id character varying(100) NOT NULL,
    node character varying(200) NOT NULL,
    mission_type character varying(100) NOT NULL,
    enemy character varying(100) NOT NULL,
    planet character varying(100) NOT NULL,
    bounds_json text NOT NULL,
    source_url text NOT NULL,
    fetched_at timestamp with time zone NOT NULL,
    CONSTRAINT ck_wf_arbitration_interval CHECK ((expiry = (activation + '01:00:00'::interval)))
);


--
-- Name: TABLE wf_arbitration_schedule; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_arbitration_schedule IS '第三方仲裁预测排期及历史存档，不是官方动态或节点精华效率表';


--
-- Name: COLUMN wf_arbitration_schedule.activation; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_arbitration_schedule.activation IS '任务开始 UTC 时间，也是跨刷新幂等主键';


--
-- Name: COLUMN wf_arbitration_schedule.expiry; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_arbitration_schedule.expiry IS '任务结束 UTC 时间，查询采用左闭右开区间';


--
-- Name: COLUMN wf_arbitration_schedule.upstream_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_arbitration_schedule.upstream_id IS '上游返回的任务标识，用于审计';


--
-- Name: COLUMN wf_arbitration_schedule.node; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_arbitration_schedule.node IS '上游节点名称，展示时由社区代码簿补充解析';


--
-- Name: COLUMN wf_arbitration_schedule.mission_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_arbitration_schedule.mission_type IS '上游任务类型';


--
-- Name: COLUMN wf_arbitration_schedule.enemy; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_arbitration_schedule.enemy IS '上游敌方阵营';


--
-- Name: COLUMN wf_arbitration_schedule.planet; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_arbitration_schedule.planet IS '上游星球，未单独提供时从节点括号提取';


--
-- Name: COLUMN wf_arbitration_schedule.bounds_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_arbitration_schedule.bounds_json IS '上游区域资源及经验加成 JSON，保留原始信息供后续使用';


--
-- Name: COLUMN wf_arbitration_schedule.source_url; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_arbitration_schedule.source_url IS '本次抓取的完整排期来源地址';


--
-- Name: COLUMN wf_arbitration_schedule.fetched_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_arbitration_schedule.fetched_at IS '本次成功抓取并入库的 UTC 时间';


--
-- Name: wf_community_arbitration_essence; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_arbitration_essence (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    node_name character varying(200) NOT NULL,
    essence_amount integer NOT NULL,
    current_json text DEFAULT '{}'::text NOT NULL,
    upstream_json text DEFAULT '{}'::text NOT NULL,
    original_json text DEFAULT '{}'::text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    record_source character varying(32) DEFAULT 'SELF_MAINTAINED'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    upstream_hash character varying(64) DEFAULT ''::character varying NOT NULL,
    source_hash character varying(64) DEFAULT ''::character varying NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    CONSTRAINT ck_wf_community_arbitration_essence_amount CHECK ((essence_amount >= 0)),
    CONSTRAINT ck_wf_community_arbitration_essence_source CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_arbitration_essence; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_arbitration_essence IS '仲裁长期规则：主动刷新 warframe-info-api routes/arbitrationEssence.js 完整 ESSENCE_TABLE，按原始节点键精确匹配';


--
-- Name: COLUMN wf_community_arbitration_essence.node_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_arbitration_essence.node_name IS 'WorldState 或展示层中的仲裁节点名';


--
-- Name: COLUMN wf_community_arbitration_essence.essence_amount; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_arbitration_essence.essence_amount IS '社区估计精华/小时，缺失规则为未知，不是官方掉落保证';


--
-- Name: COLUMN wf_community_arbitration_essence.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_arbitration_essence.record_source IS 'COMMUNITY 上游同步或 SELF_MAINTAINED 本地维护；自维护记录不参与刷新删除';


--
-- Name: wf_community_arbitration_essence_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_arbitration_essence_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_arbitration_essence_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_arbitration_essence_id_seq OWNED BY nnmbot.wf_community_arbitration_essence.id;


--
-- Name: wf_community_bounty_definition; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_bounty_definition (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    source_code character varying(200) NOT NULL,
    last_imported_at timestamp without time zone,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    name_key character varying(500),
    description_key character varying(500),
    icon character varying(500),
    CONSTRAINT ck_wf_bounty_source CHECK (((record_source)::text = ANY ((ARRAY['COMMUNITY'::character varying, 'SELF_MAINTAINED'::character varying])::text[])))
);


--
-- Name: TABLE wf_community_bounty_definition; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_bounty_definition IS 'ExportBounties 静态赏金定义，阶段明细与定义整体采纳，不替代动态赏金';


--
-- Name: COLUMN wf_community_bounty_definition.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.id IS '定义主键';


--
-- Name: COLUMN wf_community_bounty_definition.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.record_key IS 'WorldState jobType 引用的原始任务路径';


--
-- Name: COLUMN wf_community_bounty_definition.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.record_source IS '社区同步或自维护记录';


--
-- Name: COLUMN wf_community_bounty_definition.source_code; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.source_code IS '具体文件来源代码，不表示优先级';


--
-- Name: COLUMN wf_community_bounty_definition.last_imported_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.last_imported_at IS '最近人工采纳时间';


--
-- Name: COLUMN wf_community_bounty_definition.current_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.current_json IS '当前完整定义 JSON';


--
-- Name: COLUMN wf_community_bounty_definition.upstream_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.upstream_json IS '已采纳上游定义';


--
-- Name: COLUMN wf_community_bounty_definition.original_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.original_json IS '首次导入定义';


--
-- Name: COLUMN wf_community_bounty_definition.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.manual_fields_json IS '旧协议兼容字段，不用于社区字段覆盖';


--
-- Name: COLUMN wf_community_bounty_definition.upstream_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.upstream_hash IS '已采纳上游内容摘要';


--
-- Name: COLUMN wf_community_bounty_definition.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.source_hash IS '当前内容摘要';


--
-- Name: COLUMN wf_community_bounty_definition.manual_modified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.manual_modified IS '旧协议兼容字段，社区采纳固定为 false';


--
-- Name: COLUMN wf_community_bounty_definition.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.enabled IS '记录是否供查询使用';


--
-- Name: COLUMN wf_community_bounty_definition.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.created_at IS '创建时间';


--
-- Name: COLUMN wf_community_bounty_definition.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.updated_at IS '修改时间';


--
-- Name: COLUMN wf_community_bounty_definition.name_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.name_key IS '任务名称语言词条路径';


--
-- Name: COLUMN wf_community_bounty_definition.description_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.description_key IS '任务描述语言词条路径';


--
-- Name: COLUMN wf_community_bounty_definition.icon; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_definition.icon IS '原始图标引用';


--
-- Name: wf_community_bounty_definition_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

ALTER TABLE nnmbot.wf_community_bounty_definition ALTER COLUMN id ADD GENERATED BY DEFAULT AS IDENTITY (
    SEQUENCE NAME nnmbot.wf_community_bounty_definition_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1
);


--
-- Name: wf_community_bounty_stage; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_bounty_stage (
    definition_id bigint NOT NULL,
    entry_order integer NOT NULL,
    stage_number integer NOT NULL,
    option_number integer NOT NULL,
    job_reference character varying(500) NOT NULL,
    CONSTRAINT wf_community_bounty_stage_option_number_check CHECK ((option_number > 0)),
    CONSTRAINT wf_community_bounty_stage_stage_number_check CHECK ((stage_number > 0))
);


--
-- Name: TABLE wf_community_bounty_stage; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_bounty_stage IS '赏金定义的有序阶段与候选任务，不单独刷新或采纳';


--
-- Name: COLUMN wf_community_bounty_stage.definition_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_stage.definition_id IS '所属定义主键，保证不混用不同来源明细';


--
-- Name: COLUMN wf_community_bounty_stage.entry_order; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_stage.entry_order IS 'JPA 集合顺序，从 0 开始';


--
-- Name: COLUMN wf_community_bounty_stage.stage_number; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_stage.stage_number IS '阶段顺序，从 1 开始';


--
-- Name: COLUMN wf_community_bounty_stage.option_number; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_stage.option_number IS '阶段内候选顺序，从 1 开始';


--
-- Name: COLUMN wf_community_bounty_stage.job_reference; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_bounty_stage.job_reference IS '阶段候选任务路径';


--
-- Name: wf_community_command_alias; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_command_alias (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    alias_name character varying(200) NOT NULL,
    target_field character varying(200) NOT NULL,
    current_json text DEFAULT '{}'::text NOT NULL,
    upstream_json text DEFAULT '{}'::text NOT NULL,
    original_json text DEFAULT '{}'::text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    record_source character varying(32) DEFAULT 'SELF_MAINTAINED'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    upstream_hash character varying(64) DEFAULT ''::character varying NOT NULL,
    source_hash character varying(64) DEFAULT ''::character varying NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    CONSTRAINT ck_wf_community_command_alias_source CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_command_alias; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_command_alias IS 'WorldState 命令别名词库：参考项目 wfTypeAlias.json 的中文命令输入到 WorldState 字段映射';


--
-- Name: COLUMN wf_community_command_alias.alias_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_command_alias.alias_name IS '用户输入的中文命令别名';


--
-- Name: COLUMN wf_community_command_alias.target_field; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_command_alias.target_field IS 'WorldState 目标字段或命令类型';


--
-- Name: COLUMN wf_community_command_alias.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_command_alias.record_source IS 'COMMUNITY 上游同步或 SELF_MAINTAINED 本地维护';


--
-- Name: wf_community_command_alias_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_command_alias_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_command_alias_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_command_alias_id_seq OWNED BY nnmbot.wf_community_command_alias.id;


--
-- Name: wf_community_dataset_alias; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_dataset_alias (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    source_id bigint,
    alias_name character varying(500),
    target_name character varying(500),
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    normalized_alias character varying(500),
    manual_item_id bigint,
    remark character varying(1000),
    source_code character varying(200),
    last_imported_at timestamp without time zone
);


--
-- Name: TABLE wf_community_dataset_alias; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_dataset_alias IS '统一社区别名数据表：COMMUNITY 为上游同步，SELF_MAINTAINED 为管理员维护';


--
-- Name: COLUMN wf_community_dataset_alias.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.id IS '数据集记录主键';


--
-- Name: COLUMN wf_community_dataset_alias.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.record_key IS '从 JSON 推断的稳定记录标识';


--
-- Name: COLUMN wf_community_dataset_alias.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';


--
-- Name: COLUMN wf_community_dataset_alias.source_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.source_id IS '社区源记录 id';


--
-- Name: COLUMN wf_community_dataset_alias.alias_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.alias_name IS '社区中文别名';


--
-- Name: COLUMN wf_community_dataset_alias.target_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.target_name IS '别名对应的英文目标名称';


--
-- Name: COLUMN wf_community_dataset_alias.current_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.current_json IS '当前运行时生效 JSON';


--
-- Name: COLUMN wf_community_dataset_alias.upstream_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.upstream_json IS '最近一次上游 JSON';


--
-- Name: COLUMN wf_community_dataset_alias.original_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.original_json IS '首次导入时的原始 JSON';


--
-- Name: COLUMN wf_community_dataset_alias.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.manual_fields_json IS '人工修改的顶层字段及其值';


--
-- Name: COLUMN wf_community_dataset_alias.upstream_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.upstream_hash IS '上游记录 JSON SHA-256';


--
-- Name: COLUMN wf_community_dataset_alias.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.source_hash IS '数据源记录摘要，供刷新幂等判断';


--
-- Name: COLUMN wf_community_dataset_alias.manual_modified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.manual_modified IS '是否存在人工修改字段';


--
-- Name: COLUMN wf_community_dataset_alias.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.enabled IS '记录是否参与运行时查询';


--
-- Name: COLUMN wf_community_dataset_alias.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.created_at IS '首次写入时间';


--
-- Name: COLUMN wf_community_dataset_alias.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.updated_at IS '最后更新时间';


--
-- Name: COLUMN wf_community_dataset_alias.normalized_alias; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.normalized_alias IS '规范化别名；社区记录和自维护记录统一使用';


--
-- Name: COLUMN wf_community_dataset_alias.manual_item_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.manual_item_id IS '自维护别名关联的 wf_manual_item.id，社区同步记录为空';


--
-- Name: COLUMN wf_community_dataset_alias.remark; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_alias.remark IS '自维护记录备注';


--
-- Name: wf_community_dataset_alias_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_dataset_alias_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_dataset_alias_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_dataset_alias_id_seq OWNED BY nnmbot.wf_community_dataset_alias.id;


--
-- Name: wf_community_dataset_market_component; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_dataset_market_component (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    record_source character varying(32) DEFAULT 'SELF_MAINTAINED'::character varying NOT NULL,
    input_alias character varying(200) NOT NULL,
    market_suffix character varying(200) NOT NULL,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text CONSTRAINT wf_community_dataset_market_compone_manual_fields_json_not_null NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone
);


--
-- Name: TABLE wf_community_dataset_market_component; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_dataset_market_component IS 'NnmBot 自维护的 Warframe Market 组件输入后缀映射';


--
-- Name: COLUMN wf_community_dataset_market_component.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_component.id IS '数据集记录主键';


--
-- Name: COLUMN wf_community_dataset_market_component.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_component.record_key IS '稳定记录键，格式为 alias:输入后缀';


--
-- Name: COLUMN wf_community_dataset_market_component.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_component.record_source IS '固定为 SELF_MAINTAINED，表示仅由本系统维护';


--
-- Name: COLUMN wf_community_dataset_market_component.input_alias; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_component.input_alias IS '用户输入的中文组件后缀';


--
-- Name: COLUMN wf_community_dataset_market_component.market_suffix; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_component.market_suffix IS 'Warframe Market 商品名称使用的英文后缀';


--
-- Name: COLUMN wf_community_dataset_market_component.current_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_component.current_json IS '当前运行时生效 JSON';


--
-- Name: COLUMN wf_community_dataset_market_component.upstream_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_component.upstream_json IS '本地规则无社区上游，固定为空对象';


--
-- Name: COLUMN wf_community_dataset_market_component.original_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_component.original_json IS '首次创建时的原始 JSON';


--
-- Name: COLUMN wf_community_dataset_market_component.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_component.manual_fields_json IS '人工修改字段，固定为空对象';


--
-- Name: COLUMN wf_community_dataset_market_component.upstream_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_component.upstream_hash IS '本地规则无上游，保存空对象摘要';


--
-- Name: COLUMN wf_community_dataset_market_component.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_component.source_hash IS '当前规则 JSON 摘要';


--
-- Name: COLUMN wf_community_dataset_market_component.manual_modified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_component.manual_modified IS '是否存在人工修改字段';


--
-- Name: COLUMN wf_community_dataset_market_component.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_component.enabled IS '是否参与运行时查询';


--
-- Name: COLUMN wf_community_dataset_market_component.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_component.created_at IS '首次写入时间';


--
-- Name: COLUMN wf_community_dataset_market_component.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_component.updated_at IS '最后更新时间';


--
-- Name: wf_community_dataset_market_component_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_dataset_market_component_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_dataset_market_component_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_dataset_market_component_id_seq OWNED BY nnmbot.wf_community_dataset_market_component.id;


--
-- Name: wf_community_dataset_market_element; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_dataset_market_element (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    source_id bigint,
    element_code character varying(200),
    display_name character varying(200),
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone
);


--
-- Name: TABLE wf_community_dataset_market_element; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_dataset_market_element IS '社区 market_element.json 数据集记录';


--
-- Name: COLUMN wf_community_dataset_market_element.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.id IS '数据集记录主键';


--
-- Name: COLUMN wf_community_dataset_market_element.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.record_key IS '从 JSON 推断的稳定记录标识';


--
-- Name: COLUMN wf_community_dataset_market_element.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';


--
-- Name: COLUMN wf_community_dataset_market_element.source_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.source_id IS '社区源记录 id';


--
-- Name: COLUMN wf_community_dataset_market_element.element_code; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.element_code IS 'Warframe Market 元素代码';


--
-- Name: COLUMN wf_community_dataset_market_element.display_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.display_name IS '社区中文显示名称';


--
-- Name: COLUMN wf_community_dataset_market_element.current_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.current_json IS '当前运行时生效 JSON';


--
-- Name: COLUMN wf_community_dataset_market_element.upstream_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.upstream_json IS '最近一次上游 JSON';


--
-- Name: COLUMN wf_community_dataset_market_element.original_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.original_json IS '首次导入时的原始 JSON';


--
-- Name: COLUMN wf_community_dataset_market_element.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.manual_fields_json IS '人工修改的顶层字段及其值';


--
-- Name: COLUMN wf_community_dataset_market_element.upstream_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.upstream_hash IS '上游记录 JSON SHA-256';


--
-- Name: COLUMN wf_community_dataset_market_element.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.source_hash IS '数据源记录摘要，供刷新幂等判断';


--
-- Name: COLUMN wf_community_dataset_market_element.manual_modified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.manual_modified IS '是否存在人工修改字段';


--
-- Name: COLUMN wf_community_dataset_market_element.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.enabled IS '记录是否参与运行时查询';


--
-- Name: COLUMN wf_community_dataset_market_element.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.created_at IS '首次写入时间';


--
-- Name: COLUMN wf_community_dataset_market_element.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_element.updated_at IS '最后更新时间';


--
-- Name: wf_community_dataset_market_element_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_dataset_market_element_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_dataset_market_element_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_dataset_market_element_id_seq OWNED BY nnmbot.wf_community_dataset_market_element.id;


--
-- Name: wf_community_dataset_market_riven_tion; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_dataset_market_riven_tion (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    source_id bigint,
    effect character varying(500),
    attribute_group character varying(100),
    negative_only boolean,
    positive_is_negative boolean,
    prefix character varying(100),
    search_only boolean,
    suffix character varying(100),
    units character varying(100),
    url_name character varying(200),
    exclusive_to_json text,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text CONSTRAINT wf_community_dataset_market_riven_t_manual_fields_json_not_null NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone
);


--
-- Name: TABLE wf_community_dataset_market_riven_tion; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_dataset_market_riven_tion IS '社区 market_riven_tion.json 数据集记录';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.id IS '数据集记录主键';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.record_key IS '从 JSON 推断的稳定记录标识';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.source_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.source_id IS '社区源记录 ids';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.effect; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.effect IS 'Riven 词条效果说明';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.attribute_group; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.attribute_group IS 'Riven 属性分组';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.negative_only; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.negative_only IS '是否仅用于负面词条';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.positive_is_negative; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.positive_is_negative IS '正值是否按负面效果解释';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.prefix; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.prefix IS 'Riven 词条前缀';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.search_only; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.search_only IS '是否仅用于搜索匹配';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.suffix; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.suffix IS 'Riven 词条后缀';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.units; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.units IS 'Riven 词条数值单位';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.url_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.url_name IS 'Warframe Market URL 属性名';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.exclusive_to_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.exclusive_to_json IS '词条适用武器类型 JSON';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.current_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.current_json IS '当前运行时生效 JSON';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.upstream_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.upstream_json IS '最近一次上游 JSON';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.original_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.original_json IS '首次导入时的原始 JSON';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.manual_fields_json IS '人工修改的顶层字段及其值';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.upstream_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.upstream_hash IS '上游记录 JSON SHA-256';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.source_hash IS '数据源记录摘要，供刷新幂等判断';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.manual_modified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.manual_modified IS '是否存在人工修改字段';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.enabled IS '记录是否参与运行时查询';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.created_at IS '首次写入时间';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion.updated_at IS '最后更新时间';


--
-- Name: wf_community_dataset_market_riven_tion_alias; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_dataset_market_riven_tion_alias (
    id bigint NOT NULL,
    record_key character varying(500) CONSTRAINT wf_community_dataset_market_riven_tion_alia_record_key_not_null NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying CONSTRAINT wf_community_dataset_market_riven_tion_a_record_source_not_null NOT NULL,
    source_id bigint,
    attribute_code character varying(200),
    alias_name character varying(200),
    current_json text CONSTRAINT wf_community_dataset_market_riven_tion_al_current_json_not_null NOT NULL,
    upstream_json text CONSTRAINT wf_community_dataset_market_riven_tion_a_upstream_json_not_null NOT NULL,
    original_json text CONSTRAINT wf_community_dataset_market_riven_tion_a_original_json_not_null NOT NULL,
    manual_fields_json text DEFAULT '{}'::text CONSTRAINT wf_community_dataset_market_riven__manual_fields_json_not_null1 NOT NULL,
    upstream_hash character varying(64) CONSTRAINT wf_community_dataset_market_riven_tion_a_upstream_hash_not_null NOT NULL,
    source_hash character varying(64) CONSTRAINT wf_community_dataset_market_riven_tion_ali_source_hash_not_null NOT NULL,
    manual_modified boolean DEFAULT false CONSTRAINT wf_community_dataset_market_riven_tio_manual_modified_not_null1 NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone CONSTRAINT wf_community_dataset_market_riven_tion_alia_created_at_not_null NOT NULL,
    updated_at timestamp without time zone CONSTRAINT wf_community_dataset_market_riven_tion_alia_updated_at_not_null NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone
);


--
-- Name: TABLE wf_community_dataset_market_riven_tion_alias; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_dataset_market_riven_tion_alias IS '社区 market_riven_tion_alias.json 数据集记录';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.id IS '数据集记录主键';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.record_key IS '从 JSON 推断的稳定记录标识';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.source_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.source_id IS '社区源记录 id';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.attribute_code; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.attribute_code IS '一个属性代码或一到三个逗号分隔的组合属性代码；由管理端维护';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.alias_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.alias_name IS 'Riven 属性中文别名';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.current_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.current_json IS '当前运行时生效 JSON';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.upstream_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.upstream_json IS '最近一次上游 JSON';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.original_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.original_json IS '首次导入时的原始 JSON';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.manual_fields_json IS '人工修改的顶层字段及其值';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.upstream_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.upstream_hash IS '上游记录 JSON SHA-256';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.source_hash IS '数据源记录摘要，供刷新幂等判断';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.manual_modified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.manual_modified IS '是否存在人工修改字段';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.enabled IS '记录是否参与运行时查询';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.created_at IS '首次写入时间';


--
-- Name: COLUMN wf_community_dataset_market_riven_tion_alias.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_market_riven_tion_alias.updated_at IS '最后更新时间';


--
-- Name: wf_community_dataset_market_riven_tion_alias_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_dataset_market_riven_tion_alias_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_dataset_market_riven_tion_alias_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_dataset_market_riven_tion_alias_id_seq OWNED BY nnmbot.wf_community_dataset_market_riven_tion_alias.id;


--
-- Name: wf_community_dataset_market_riven_tion_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_dataset_market_riven_tion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_dataset_market_riven_tion_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_dataset_market_riven_tion_id_seq OWNED BY nnmbot.wf_community_dataset_market_riven_tion.id;


--
-- Name: wf_community_dataset_nodes; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_dataset_nodes (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    unique_name character varying(500),
    display_name character varying(500),
    system_index integer,
    system_name character varying(200),
    node_type integer,
    mastery_req integer,
    mission_index integer,
    faction_index integer,
    min_enemy_level integer,
    max_enemy_level integer,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone
);


--
-- Name: TABLE wf_community_dataset_nodes; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_dataset_nodes IS '社区 nodes.json 数据集记录';


--
-- Name: COLUMN wf_community_dataset_nodes.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.id IS '数据集记录主键';


--
-- Name: COLUMN wf_community_dataset_nodes.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.record_key IS '从 JSON 推断的稳定记录标识';


--
-- Name: COLUMN wf_community_dataset_nodes.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';


--
-- Name: COLUMN wf_community_dataset_nodes.unique_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.unique_name IS '游戏内部节点 uniqueName';


--
-- Name: COLUMN wf_community_dataset_nodes.display_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.display_name IS '社区节点显示名称';


--
-- Name: COLUMN wf_community_dataset_nodes.system_index; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.system_index IS '节点所属星系索引';


--
-- Name: COLUMN wf_community_dataset_nodes.system_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.system_name IS '节点所属星系名称';


--
-- Name: COLUMN wf_community_dataset_nodes.node_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.node_type IS '节点类型索引';


--
-- Name: COLUMN wf_community_dataset_nodes.mastery_req; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.mastery_req IS '节点段位要求';


--
-- Name: COLUMN wf_community_dataset_nodes.mission_index; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.mission_index IS '节点任务类型索引';


--
-- Name: COLUMN wf_community_dataset_nodes.faction_index; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.faction_index IS '节点敌对派系索引';


--
-- Name: COLUMN wf_community_dataset_nodes.min_enemy_level; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.min_enemy_level IS '节点最低敌人等级';


--
-- Name: COLUMN wf_community_dataset_nodes.max_enemy_level; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.max_enemy_level IS '节点最高敌人等级';


--
-- Name: COLUMN wf_community_dataset_nodes.current_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.current_json IS '当前运行时生效 JSON';


--
-- Name: COLUMN wf_community_dataset_nodes.upstream_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.upstream_json IS '最近一次上游 JSON';


--
-- Name: COLUMN wf_community_dataset_nodes.original_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.original_json IS '首次导入时的原始 JSON';


--
-- Name: COLUMN wf_community_dataset_nodes.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.manual_fields_json IS '人工修改的顶层字段及其值';


--
-- Name: COLUMN wf_community_dataset_nodes.upstream_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.upstream_hash IS '上游记录 JSON SHA-256';


--
-- Name: COLUMN wf_community_dataset_nodes.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.source_hash IS '数据源记录摘要，供刷新幂等判断';


--
-- Name: COLUMN wf_community_dataset_nodes.manual_modified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.manual_modified IS '是否存在人工修改字段';


--
-- Name: COLUMN wf_community_dataset_nodes.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.enabled IS '记录是否参与运行时查询';


--
-- Name: COLUMN wf_community_dataset_nodes.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.created_at IS '首次写入时间';


--
-- Name: COLUMN wf_community_dataset_nodes.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_nodes.updated_at IS '最后更新时间';


--
-- Name: wf_community_dataset_nodes_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_dataset_nodes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_dataset_nodes_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_dataset_nodes_id_seq OWNED BY nnmbot.wf_community_dataset_nodes.id;


--
-- Name: wf_community_dataset_reward_pool; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_dataset_reward_pool (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    unique_name character varying(500),
    reward_count integer,
    rewards_json text,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone
);


--
-- Name: TABLE wf_community_dataset_reward_pool; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_dataset_reward_pool IS '社区奖励池；赏金补充来源为 public-export-plus ExportRewards，rewards 数组保留 stage、item、count、chance（百分数），JSON.source 记录来源';


--
-- Name: COLUMN wf_community_dataset_reward_pool.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.id IS '数据集记录主键';


--
-- Name: COLUMN wf_community_dataset_reward_pool.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.record_key IS '从 JSON 推断的稳定记录标识';


--
-- Name: COLUMN wf_community_dataset_reward_pool.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';


--
-- Name: COLUMN wf_community_dataset_reward_pool.unique_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.unique_name IS '游戏内部奖励池 uniqueName';


--
-- Name: COLUMN wf_community_dataset_reward_pool.reward_count; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.reward_count IS '奖励数组元素数量';


--
-- Name: COLUMN wf_community_dataset_reward_pool.rewards_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.rewards_json IS '完整奖励数组 JSON';


--
-- Name: COLUMN wf_community_dataset_reward_pool.current_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.current_json IS '当前运行时生效 JSON';


--
-- Name: COLUMN wf_community_dataset_reward_pool.upstream_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.upstream_json IS '最近一次上游 JSON';


--
-- Name: COLUMN wf_community_dataset_reward_pool.original_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.original_json IS '首次导入时的原始 JSON';


--
-- Name: COLUMN wf_community_dataset_reward_pool.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.manual_fields_json IS '人工修改的顶层字段及其值';


--
-- Name: COLUMN wf_community_dataset_reward_pool.upstream_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.upstream_hash IS '上游记录 JSON SHA-256';


--
-- Name: COLUMN wf_community_dataset_reward_pool.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.source_hash IS '数据源记录摘要，供刷新幂等判断';


--
-- Name: COLUMN wf_community_dataset_reward_pool.manual_modified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.manual_modified IS '是否存在人工修改字段';


--
-- Name: COLUMN wf_community_dataset_reward_pool.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.enabled IS '记录是否参与运行时查询';


--
-- Name: COLUMN wf_community_dataset_reward_pool.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.created_at IS '首次写入时间';


--
-- Name: COLUMN wf_community_dataset_reward_pool.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_reward_pool.updated_at IS '最后更新时间';


--
-- Name: wf_community_dataset_reward_pool_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_dataset_reward_pool_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_dataset_reward_pool_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_dataset_reward_pool_id_seq OWNED BY nnmbot.wf_community_dataset_reward_pool.id;


--
-- Name: wf_community_dataset_riven_analyse_trend; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_dataset_riven_analyse_trend (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    source_id bigint,
    display_name character varying(500),
    prefix character varying(100),
    suffix character varying(100),
    archwing double precision,
    melee_value double precision,
    pistol double precision,
    rifle double precision,
    shotgun double precision,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text CONSTRAINT wf_community_dataset_riven_analyse__manual_fields_json_not_null NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    manual_modified boolean DEFAULT false CONSTRAINT wf_community_dataset_riven_analyse_tre_manual_modified_not_null NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone
);


--
-- Name: TABLE wf_community_dataset_riven_analyse_trend; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_dataset_riven_analyse_trend IS '社区 riven_analyse_trend.json 数据集记录';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.id IS '数据集记录主键';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.record_key IS '从 JSON 推断的稳定记录标识';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.source_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.source_id IS '社区源记录 id';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.display_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.display_name IS 'Riven 分析趋势名称';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.prefix; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.prefix IS 'Riven 分析词条前缀';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.suffix; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.suffix IS 'Riven 分析词条后缀';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.archwing; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.archwing IS 'Archwing 武器趋势系数';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.melee_value; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.melee_value IS '近战武器趋势系数，对应上游 melle 字段';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.pistol; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.pistol IS '手枪趋势系数';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.rifle; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.rifle IS '步枪趋势系数';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.shotgun; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.shotgun IS '霰弹枪趋势系数';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.current_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.current_json IS '当前运行时生效 JSON';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.upstream_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.upstream_json IS '最近一次上游 JSON';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.original_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.original_json IS '首次导入时的原始 JSON';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.manual_fields_json IS '人工修改的顶层字段及其值';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.upstream_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.upstream_hash IS '上游记录 JSON SHA-256';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.source_hash IS '数据源记录摘要，供刷新幂等判断';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.manual_modified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.manual_modified IS '是否存在人工修改字段';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.enabled IS '记录是否参与运行时查询';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.created_at IS '首次写入时间';


--
-- Name: COLUMN wf_community_dataset_riven_analyse_trend.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_analyse_trend.updated_at IS '最后更新时间';


--
-- Name: wf_community_dataset_riven_analyse_trend_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_dataset_riven_analyse_trend_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_dataset_riven_analyse_trend_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_dataset_riven_analyse_trend_id_seq OWNED BY nnmbot.wf_community_dataset_riven_analyse_trend.id;


--
-- Name: wf_community_dataset_riven_trend; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_dataset_riven_trend (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    source_id bigint,
    trend_time character varying(32),
    new_dot character varying(32),
    new_num double precision,
    old_dot character varying(32),
    old_num double precision,
    trend_name character varying(500),
    weapon_type character varying(100),
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone
);


--
-- Name: TABLE wf_community_dataset_riven_trend; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_dataset_riven_trend IS '社区 riven_trend.json 数据集记录';


--
-- Name: COLUMN wf_community_dataset_riven_trend.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.id IS '数据集记录主键';


--
-- Name: COLUMN wf_community_dataset_riven_trend.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.record_key IS '从 JSON 推断的稳定记录标识';


--
-- Name: COLUMN wf_community_dataset_riven_trend.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';


--
-- Name: COLUMN wf_community_dataset_riven_trend.source_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.source_id IS '社区源记录 id';


--
-- Name: COLUMN wf_community_dataset_riven_trend.trend_time; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.trend_time IS 'Riven 趋势时间，对应上游 isDate 字段';


--
-- Name: COLUMN wf_community_dataset_riven_trend.new_dot; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.new_dot IS '变化后的倾向点位';


--
-- Name: COLUMN wf_community_dataset_riven_trend.new_num; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.new_num IS '变化后的倾向数值';


--
-- Name: COLUMN wf_community_dataset_riven_trend.old_dot; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.old_dot IS '变化前的倾向点位';


--
-- Name: COLUMN wf_community_dataset_riven_trend.old_num; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.old_num IS '变化前的倾向数值';


--
-- Name: COLUMN wf_community_dataset_riven_trend.trend_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.trend_name IS 'Riven 趋势名称';


--
-- Name: COLUMN wf_community_dataset_riven_trend.weapon_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.weapon_type IS 'Riven 趋势武器类型';


--
-- Name: COLUMN wf_community_dataset_riven_trend.current_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.current_json IS '当前运行时生效 JSON';


--
-- Name: COLUMN wf_community_dataset_riven_trend.upstream_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.upstream_json IS '最近一次上游 JSON';


--
-- Name: COLUMN wf_community_dataset_riven_trend.original_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.original_json IS '首次导入时的原始 JSON';


--
-- Name: COLUMN wf_community_dataset_riven_trend.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.manual_fields_json IS '人工修改的顶层字段及其值';


--
-- Name: COLUMN wf_community_dataset_riven_trend.upstream_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.upstream_hash IS '上游记录 JSON SHA-256';


--
-- Name: COLUMN wf_community_dataset_riven_trend.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.source_hash IS '数据源记录摘要，供刷新幂等判断';


--
-- Name: COLUMN wf_community_dataset_riven_trend.manual_modified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.manual_modified IS '是否存在人工修改字段';


--
-- Name: COLUMN wf_community_dataset_riven_trend.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.enabled IS '记录是否参与运行时查询';


--
-- Name: COLUMN wf_community_dataset_riven_trend.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.created_at IS '首次写入时间';


--
-- Name: COLUMN wf_community_dataset_riven_trend.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_riven_trend.updated_at IS '最后更新时间';


--
-- Name: wf_community_dataset_riven_trend_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_dataset_riven_trend_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_dataset_riven_trend_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_dataset_riven_trend_id_seq OWNED BY nnmbot.wf_community_dataset_riven_trend.id;


--
-- Name: wf_community_dataset_state_translation; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_dataset_state_translation (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    unique_name character varying(500),
    display_name character varying(500),
    description text,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text CONSTRAINT wf_community_dataset_state_translat_manual_fields_json_not_null NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone
);


--
-- Name: TABLE wf_community_dataset_state_translation; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_dataset_state_translation IS '社区世界状态名称及说明；赏金补充来自 public-export-plus ExportBounties，夜波补充来自 ExportChallenges 和简体 dict.zh，JSON.source 记录来源与校验值';


--
-- Name: COLUMN wf_community_dataset_state_translation.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.id IS '数据集记录主键';


--
-- Name: COLUMN wf_community_dataset_state_translation.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.record_key IS '从 JSON 推断的稳定记录标识';


--
-- Name: COLUMN wf_community_dataset_state_translation.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';


--
-- Name: COLUMN wf_community_dataset_state_translation.unique_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.unique_name IS '动态世界状态内部 uniqueName';


--
-- Name: COLUMN wf_community_dataset_state_translation.display_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.display_name IS '动态世界状态显示名称';


--
-- Name: COLUMN wf_community_dataset_state_translation.description; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.description IS '动态世界状态说明';


--
-- Name: COLUMN wf_community_dataset_state_translation.current_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.current_json IS '当前运行时生效 JSON';


--
-- Name: COLUMN wf_community_dataset_state_translation.upstream_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.upstream_json IS '最近一次上游 JSON';


--
-- Name: COLUMN wf_community_dataset_state_translation.original_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.original_json IS '首次导入时的原始 JSON';


--
-- Name: COLUMN wf_community_dataset_state_translation.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.manual_fields_json IS '人工修改的顶层字段及其值';


--
-- Name: COLUMN wf_community_dataset_state_translation.upstream_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.upstream_hash IS '上游记录 JSON SHA-256';


--
-- Name: COLUMN wf_community_dataset_state_translation.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.source_hash IS '数据源记录摘要，供刷新幂等判断';


--
-- Name: COLUMN wf_community_dataset_state_translation.manual_modified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.manual_modified IS '是否存在人工修改字段';


--
-- Name: COLUMN wf_community_dataset_state_translation.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.enabled IS '记录是否参与运行时查询';


--
-- Name: COLUMN wf_community_dataset_state_translation.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.created_at IS '首次写入时间';


--
-- Name: COLUMN wf_community_dataset_state_translation.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_state_translation.updated_at IS '最后更新时间';


--
-- Name: wf_community_dataset_state_translation_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_dataset_state_translation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_dataset_state_translation_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_dataset_state_translation_id_seq OWNED BY nnmbot.wf_community_dataset_state_translation.id;


--
-- Name: wf_community_dataset_translation; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_dataset_translation (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    source_id bigint,
    locale character varying(16),
    chinese_name character varying(500),
    english_name character varying(500),
    prime_item boolean,
    set_item boolean,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone
);


--
-- Name: TABLE wf_community_dataset_translation; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_dataset_translation IS '社区 translation.json 数据集记录';


--
-- Name: COLUMN wf_community_dataset_translation.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.id IS '数据集记录主键';


--
-- Name: COLUMN wf_community_dataset_translation.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.record_key IS '从 JSON 推断的稳定记录标识';


--
-- Name: COLUMN wf_community_dataset_translation.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.record_source IS '记录维护来源：COMMUNITY 社区数据，SELF_MAINTAINED 系统自维护数据';


--
-- Name: COLUMN wf_community_dataset_translation.source_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.source_id IS '社区源记录 id';


--
-- Name: COLUMN wf_community_dataset_translation.locale; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.locale IS '翻译记录语言区域，例如 zh 或 en';


--
-- Name: COLUMN wf_community_dataset_translation.chinese_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.chinese_name IS '社区中文名称';


--
-- Name: COLUMN wf_community_dataset_translation.english_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.english_name IS '社区英文名称';


--
-- Name: COLUMN wf_community_dataset_translation.prime_item; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.prime_item IS '是否为 Prime 物品';


--
-- Name: COLUMN wf_community_dataset_translation.set_item; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.set_item IS '是否为套装物品';


--
-- Name: COLUMN wf_community_dataset_translation.current_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.current_json IS '当前运行时生效 JSON';


--
-- Name: COLUMN wf_community_dataset_translation.upstream_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.upstream_json IS '最近一次上游 JSON';


--
-- Name: COLUMN wf_community_dataset_translation.original_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.original_json IS '首次导入时的原始 JSON';


--
-- Name: COLUMN wf_community_dataset_translation.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.manual_fields_json IS '人工修改的顶层字段及其值';


--
-- Name: COLUMN wf_community_dataset_translation.upstream_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.upstream_hash IS '上游记录 JSON SHA-256';


--
-- Name: COLUMN wf_community_dataset_translation.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.source_hash IS '数据源记录摘要，供刷新幂等判断';


--
-- Name: COLUMN wf_community_dataset_translation.manual_modified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.manual_modified IS '是否存在人工修改字段';


--
-- Name: COLUMN wf_community_dataset_translation.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.enabled IS '记录是否参与运行时查询';


--
-- Name: COLUMN wf_community_dataset_translation.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.created_at IS '首次写入时间';


--
-- Name: COLUMN wf_community_dataset_translation.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_dataset_translation.updated_at IS '最后更新时间';


--
-- Name: wf_community_dataset_translation_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_dataset_translation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_dataset_translation_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_dataset_translation_id_seq OWNED BY nnmbot.wf_community_dataset_translation.id;


--
-- Name: wf_community_fish_guide; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_fish_guide (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    name character varying(200),
    source_hash character varying(64) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    upstream_hash character varying(64) DEFAULT repeat('0'::text, 64) NOT NULL,
    CONSTRAINT ck_wf_community_fish_guide_source CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_fish_guide; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_fish_guide IS '社区维护的鱼类捕获攻略，包含地点、时间、鱼饵、鱼叉、资源和声望信息';


--
-- Name: COLUMN wf_community_fish_guide.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_fish_guide.record_key IS '攻略稳定键';


--
-- Name: COLUMN wf_community_fish_guide.name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_fish_guide.name IS '鱼类中文或英文名称';


--
-- Name: COLUMN wf_community_fish_guide.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_fish_guide.source_hash IS '来源记录 SHA-256';


--
-- Name: COLUMN wf_community_fish_guide.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_fish_guide.record_source IS 'COMMUNITY 同步数据或 SELF_MAINTAINED 自维护数据';


--
-- Name: COLUMN wf_community_fish_guide.manual_modified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_fish_guide.manual_modified IS '是否存在人工修改';


--
-- Name: COLUMN wf_community_fish_guide.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_fish_guide.enabled IS '是否参与运行时查询';


--
-- Name: wf_community_fish_guide_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_fish_guide_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_fish_guide_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_fish_guide_id_seq OWNED BY nnmbot.wf_community_fish_guide.id;


--
-- Name: wf_community_map_feature; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_map_feature (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    world character varying(32) NOT NULL,
    feature_type character varying(80),
    name character varying(300),
    geometry_type character varying(32),
    source_hash character varying(64) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    upstream_hash character varying(64) DEFAULT repeat('0'::text, 64) NOT NULL,
    CONSTRAINT ck_wf_community_map_feature_source CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_map_feature; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_map_feature IS '社区维护的地球、夜灵平原、魔胎之境和金星地图标注';


--
-- Name: COLUMN wf_community_map_feature.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_map_feature.record_key IS '地图标注稳定键';


--
-- Name: COLUMN wf_community_map_feature.world; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_map_feature.world IS '地图区域';


--
-- Name: COLUMN wf_community_map_feature.feature_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_map_feature.feature_type IS '标注类型';


--
-- Name: COLUMN wf_community_map_feature.name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_map_feature.name IS '标注名称';


--
-- Name: COLUMN wf_community_map_feature.geometry_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_map_feature.geometry_type IS 'GeoJSON 几何类型';


--
-- Name: COLUMN wf_community_map_feature.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_map_feature.source_hash IS '来源记录 SHA-256';


--
-- Name: COLUMN wf_community_map_feature.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_map_feature.record_source IS 'COMMUNITY 同步数据或 SELF_MAINTAINED 自维护数据';


--
-- Name: wf_community_map_feature_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_map_feature_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_map_feature_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_map_feature_id_seq OWNED BY nnmbot.wf_community_map_feature.id;


--
-- Name: wf_community_merge_receipt; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_merge_receipt (
    preview_id character varying(36) NOT NULL,
    dataset character varying(100) NOT NULL,
    committed_at timestamp with time zone NOT NULL
);


--
-- Name: TABLE wf_community_merge_receipt; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_merge_receipt IS '社区预览采纳的持久幂等回执';


--
-- Name: COLUMN wf_community_merge_receipt.preview_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_merge_receipt.preview_id IS '唯一预览批次 ID，阻止重复提交';


--
-- Name: COLUMN wf_community_merge_receipt.dataset; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_merge_receipt.dataset IS '采纳的数据集代码';


--
-- Name: COLUMN wf_community_merge_receipt.committed_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_merge_receipt.committed_at IS '采纳事务记录时间';


--
-- Name: wf_community_relic; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_relic (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    relic_name character varying(200) NOT NULL,
    unique_name character varying(500),
    category character varying(80),
    era character varying(80),
    icon character varying(500),
    codex_secret boolean,
    description_key character varying(500),
    quality character varying(40),
    reward_manifest character varying(500),
    introduced_at bigint,
    market_id character varying(100),
    url_name character varying(200),
    vaulted boolean,
    source_hash character varying(64) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    upstream_hash character varying(64) DEFAULT repeat('0'::text, 64) NOT NULL,
    CONSTRAINT ck_wf_community_relic_source CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_relic; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_relic IS '社区遗物主数据，包含遗物标识、市场标识和 Vault 状态';


--
-- Name: COLUMN wf_community_relic.category; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic.category IS '遗物分类，例如普通遗物或其他来源分类';


--
-- Name: COLUMN wf_community_relic.era; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic.era IS '遗物时代，例如 Lith、Meso、Neo 或 Axi';


--
-- Name: COLUMN wf_community_relic.icon; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic.icon IS '遗物图标路径或资源地址';


--
-- Name: COLUMN wf_community_relic.codex_secret; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic.codex_secret IS '是否为 Codex 隐藏遗物';


--
-- Name: COLUMN wf_community_relic.description_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic.description_key IS '遗物描述的本地化键';


--
-- Name: COLUMN wf_community_relic.quality; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic.quality IS '遗物精炼质量或来源质量标识';


--
-- Name: COLUMN wf_community_relic.reward_manifest; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic.reward_manifest IS '遗物奖励清单或奖励文件引用';


--
-- Name: COLUMN wf_community_relic.introduced_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic.introduced_at IS '遗物引入时间戳，按来源原始单位保存';


--
-- Name: COLUMN wf_community_relic.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic.record_source IS 'COMMUNITY 同步数据或 SELF_MAINTAINED 自维护数据';


--
-- Name: wf_community_relic_drop; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_relic_drop (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    relic_name character varying(200) NOT NULL,
    relic_record_key character varying(500),
    location_text character varying(700),
    rarity character varying(32),
    chance numeric(12,4),
    drop_order integer,
    source_hash character varying(64) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    upstream_hash character varying(64) DEFAULT repeat('0'::text, 64) NOT NULL,
    CONSTRAINT ck_wf_community_relic_drop_source CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_relic_drop; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_relic_drop IS '社区整理的遗物掉落位置、稀有度及概率数据；与官方遗物奖励主数据分工不同';


--
-- Name: COLUMN wf_community_relic_drop.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic_drop.record_key IS '遗物掉落记录稳定键';


--
-- Name: COLUMN wf_community_relic_drop.relic_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic_drop.relic_name IS '遗物名称';


--
-- Name: COLUMN wf_community_relic_drop.relic_record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic_drop.relic_record_key IS '所属遗物稳定键';


--
-- Name: COLUMN wf_community_relic_drop.location_text; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic_drop.location_text IS '遗物可掉落节点及轮次';


--
-- Name: COLUMN wf_community_relic_drop.rarity; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic_drop.rarity IS '掉落稀有度';


--
-- Name: COLUMN wf_community_relic_drop.chance; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic_drop.chance IS '掉落概率，单位为百分比';


--
-- Name: COLUMN wf_community_relic_drop.drop_order; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic_drop.drop_order IS '来源文件中的掉落位置顺序';


--
-- Name: COLUMN wf_community_relic_drop.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic_drop.source_hash IS '来源记录 SHA-256';


--
-- Name: COLUMN wf_community_relic_drop.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic_drop.record_source IS 'COMMUNITY 同步数据或 SELF_MAINTAINED 自维护数据';


--
-- Name: wf_community_relic_drop_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_relic_drop_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_relic_drop_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_relic_drop_id_seq OWNED BY nnmbot.wf_community_relic_drop.id;


--
-- Name: wf_community_relic_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_relic_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_relic_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_relic_id_seq OWNED BY nnmbot.wf_community_relic.id;


--
-- Name: wf_community_relic_reward; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_relic_reward (
    id bigint NOT NULL,
    record_key character varying(700) NOT NULL,
    relic_record_key character varying(500) NOT NULL,
    reward_name character varying(300),
    unique_name character varying(500),
    market_id character varying(100),
    url_name character varying(200),
    rarity character varying(32),
    chance numeric(10,4),
    reward_order integer NOT NULL,
    source_hash character varying(64) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    upstream_hash character varying(64) DEFAULT repeat('0'::text, 64) NOT NULL,
    CONSTRAINT ck_wf_community_relic_reward_source CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_relic_reward; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_relic_reward IS '社区遗物奖励明细，供遗物、金垃圾和银垃圾命令查询';


--
-- Name: COLUMN wf_community_relic_reward.relic_record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic_reward.relic_record_key IS '关联遗物 record_key';


--
-- Name: COLUMN wf_community_relic_reward.rarity; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic_reward.rarity IS '奖励稀有度，例如 Rare 或 Uncommon';


--
-- Name: COLUMN wf_community_relic_reward.chance; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic_reward.chance IS '奖励出现概率';


--
-- Name: COLUMN wf_community_relic_reward.reward_order; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_relic_reward.reward_order IS '原始奖励顺序';


--
-- Name: wf_community_relic_reward_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_relic_reward_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_relic_reward_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_relic_reward_id_seq OWNED BY nnmbot.wf_community_relic_reward.id;


--
-- Name: wf_community_riven_unrollable; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_riven_unrollable (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    attribute_code character varying(300) NOT NULL,
    source_hash character varying(64) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    upstream_hash character varying(64) DEFAULT repeat('0'::text, 64) NOT NULL,
    CONSTRAINT ck_wf_community_riven_unrollable_source CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_riven_unrollable; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_riven_unrollable IS 'Public Export Plus 维护的不可展开 Riven 属性清单';


--
-- Name: COLUMN wf_community_riven_unrollable.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_riven_unrollable.record_key IS '武器或能力稳定键';


--
-- Name: COLUMN wf_community_riven_unrollable.attribute_code; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_riven_unrollable.attribute_code IS '官方能力属性代码';


--
-- Name: COLUMN wf_community_riven_unrollable.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_riven_unrollable.source_hash IS '来源记录 SHA-256';


--
-- Name: wf_community_riven_unrollable_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_riven_unrollable_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_riven_unrollable_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_riven_unrollable_id_seq OWNED BY nnmbot.wf_community_riven_unrollable.id;


--
-- Name: wf_community_term; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_term (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    term_group character varying(64) NOT NULL,
    english_text character varying(500),
    chinese_text character varying(500),
    current_json text DEFAULT '{}'::text NOT NULL,
    upstream_json text DEFAULT '{}'::text NOT NULL,
    original_json text DEFAULT '{}'::text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    record_source character varying(32) DEFAULT 'SELF_MAINTAINED'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    upstream_hash character varying(64) DEFAULT ''::character varying NOT NULL,
    source_hash character varying(64) DEFAULT ''::character varying NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    CONSTRAINT ck_wf_community_term_source CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_term; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_term IS '社区术语词库：warframe-info-api 的 wfExtraTerms、archimedeaTerms、RivenStatsName 和 WmRivenAttribute 等本地补充映射';


--
-- Name: COLUMN wf_community_term.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_term.record_key IS '术语稳定键，通常为英文原文或词典键';


--
-- Name: COLUMN wf_community_term.term_group; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_term.term_group IS '术语分组，例如 EXTRA、ARCHIMEDEA、RIVEN_STAT、RIVEN_ATTRIBUTE';


--
-- Name: COLUMN wf_community_term.english_text; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_term.english_text IS '英文术语或来源词';


--
-- Name: COLUMN wf_community_term.chinese_text; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_term.chinese_text IS '中文展示词；对外输出优先使用该字段';


--
-- Name: COLUMN wf_community_term.current_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_term.current_json IS '当前生效记录 JSON';


--
-- Name: COLUMN wf_community_term.upstream_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_term.upstream_json IS '最近一次上游记录 JSON';


--
-- Name: COLUMN wf_community_term.original_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_term.original_json IS '首次导入记录 JSON';


--
-- Name: COLUMN wf_community_term.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_term.manual_fields_json IS '人工修改字段 JSON';


--
-- Name: COLUMN wf_community_term.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_term.record_source IS 'COMMUNITY 上游同步，SELF_MAINTAINED 本地维护；自维护记录不参与上游比对';


--
-- Name: wf_community_term_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_term_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_term_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_term_id_seq OWNED BY nnmbot.wf_community_term.id;


--
-- Name: wf_community_worldstate_faction; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_worldstate_faction (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    locale character varying(16) NOT NULL,
    display_name character varying(500) NOT NULL,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    CONSTRAINT wf_community_worldstate_faction_record_source_check CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_worldstate_faction; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_worldstate_faction IS 'WorldState 阵营代码解释';


--
-- Name: wf_community_worldstate_faction_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_worldstate_faction_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_worldstate_faction_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_worldstate_faction_id_seq OWNED BY nnmbot.wf_community_worldstate_faction.id;


--
-- Name: wf_community_worldstate_fissure_modifier; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_worldstate_fissure_modifier (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    locale character varying(16) NOT NULL,
    display_name character varying(200) NOT NULL,
    tier_number integer DEFAULT 0 NOT NULL,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text CONSTRAINT wf_community_worldstate_fissure_mod_manual_fields_json_not_null NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false CONSTRAINT wf_community_worldstate_fissure_modifi_manual_modified_not_null NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    CONSTRAINT wf_community_worldstate_fissure_modifier_record_source_check CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_worldstate_fissure_modifier; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_worldstate_fissure_modifier IS 'WorldState 裂隙等级代码解释';


--
-- Name: COLUMN wf_community_worldstate_fissure_modifier.tier_number; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_fissure_modifier.tier_number IS '裂隙等级数字';


--
-- Name: wf_community_worldstate_fissure_modifier_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_worldstate_fissure_modifier_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_worldstate_fissure_modifier_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_worldstate_fissure_modifier_id_seq OWNED BY nnmbot.wf_community_worldstate_fissure_modifier.id;


--
-- Name: wf_community_worldstate_language; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_worldstate_language (
    id bigint NOT NULL,
    language_key character varying(500) NOT NULL,
    locale character varying(16) NOT NULL,
    display_text text NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    current_json text DEFAULT '{}'::text NOT NULL,
    upstream_json text DEFAULT '{}'::text NOT NULL,
    original_json text DEFAULT '{}'::text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    upstream_hash character varying(64) DEFAULT ''::character varying NOT NULL,
    source_hash character varying(64) DEFAULT ''::character varying NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    display_text_en text,
    display_text_zh text,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    CONSTRAINT ck_wf_community_worldstate_language_locale CHECK (((locale)::text = ANY (ARRAY[('zh'::character varying)::text, ('en'::character varying)::text]))),
    CONSTRAINT ck_wf_community_worldstate_language_source CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_worldstate_language; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_worldstate_language IS '社区 WFCD WorldState 语言键代码簿，仅提供中文和英文，中文缺失时回退英文';


--
-- Name: COLUMN wf_community_worldstate_language.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_language.id IS '语言键记录主键';


--
-- Name: COLUMN wf_community_worldstate_language.language_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_language.language_key IS 'WFCD languages.json 的稳定语言键，例如 /Lotus/Language/...';


--
-- Name: COLUMN wf_community_worldstate_language.locale; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_language.locale IS '业务支持的语言区域，仅 zh 或 en';


--
-- Name: COLUMN wf_community_worldstate_language.display_text; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_language.display_text IS '语言键对应的显示文本';


--
-- Name: COLUMN wf_community_worldstate_language.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_language.record_source IS 'COMMUNITY 上游同步，SELF_MAINTAINED 自维护';


--
-- Name: COLUMN wf_community_worldstate_language.current_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_language.current_json IS '当前运行时生效 JSON';


--
-- Name: COLUMN wf_community_worldstate_language.upstream_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_language.upstream_json IS '最近一次上游 JSON';


--
-- Name: COLUMN wf_community_worldstate_language.original_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_language.original_json IS '首次导入时的原始 JSON';


--
-- Name: COLUMN wf_community_worldstate_language.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_language.manual_fields_json IS '人工修改字段快照；SELF_MAINTAINED 记录不参与上游同步比对';


--
-- Name: COLUMN wf_community_worldstate_language.manual_modified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_language.manual_modified IS '是否存在人工修改字段';


--
-- Name: COLUMN wf_community_worldstate_language.upstream_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_language.upstream_hash IS '上游记录 JSON SHA-256';


--
-- Name: COLUMN wf_community_worldstate_language.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_language.source_hash IS '数据源记录摘要';


--
-- Name: COLUMN wf_community_worldstate_language.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_language.enabled IS '是否参与运行时查询';


--
-- Name: COLUMN wf_community_worldstate_language.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_language.created_at IS '首次写入时间';


--
-- Name: COLUMN wf_community_worldstate_language.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_language.updated_at IS '最后更新时间';


--
-- Name: wf_community_worldstate_language_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_worldstate_language_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_worldstate_language_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_worldstate_language_id_seq OWNED BY nnmbot.wf_community_worldstate_language.id;


--
-- Name: wf_community_worldstate_mission_type; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_worldstate_mission_type (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    locale character varying(16) NOT NULL,
    display_name character varying(500) NOT NULL,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text CONSTRAINT wf_community_worldstate_mission_typ_manual_fields_json_not_null NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    CONSTRAINT wf_community_worldstate_mission_type_record_source_check CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_worldstate_mission_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_worldstate_mission_type IS 'WorldState 任务类型代码解释';


--
-- Name: COLUMN wf_community_worldstate_mission_type.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_mission_type.manual_fields_json IS '人工字段快照；自维护记录不参与同步比对';


--
-- Name: COLUMN wf_community_worldstate_mission_type.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_mission_type.record_source IS 'COMMUNITY 上游同步，SELF_MAINTAINED 自维护';


--
-- Name: wf_community_worldstate_mission_type_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_worldstate_mission_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_worldstate_mission_type_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_worldstate_mission_type_id_seq OWNED BY nnmbot.wf_community_worldstate_mission_type.id;


--
-- Name: wf_community_worldstate_node; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_worldstate_node (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    locale character varying(16) NOT NULL,
    display_name character varying(500) NOT NULL,
    faction_reference character varying(200) DEFAULT ''::character varying NOT NULL,
    mission_type_reference character varying(200) DEFAULT ''::character varying NOT NULL,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    CONSTRAINT wf_community_worldstate_node_record_source_check CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_worldstate_node; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_worldstate_node IS 'WorldState 星图节点代码解释';


--
-- Name: COLUMN wf_community_worldstate_node.faction_reference; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_node.faction_reference IS '节点对应阵营代码';


--
-- Name: COLUMN wf_community_worldstate_node.mission_type_reference; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_node.mission_type_reference IS '节点对应任务类型代码';


--
-- Name: wf_community_worldstate_node_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_worldstate_node_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_worldstate_node_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_worldstate_node_id_seq OWNED BY nnmbot.wf_community_worldstate_node.id;


--
-- Name: wf_community_worldstate_operation_type; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_worldstate_operation_type (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    locale character varying(16) NOT NULL,
    display_name character varying(500) NOT NULL,
    description character varying(2000) DEFAULT ''::character varying NOT NULL,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text CONSTRAINT wf_community_worldstate_operation_t_manual_fields_json_not_null NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    CONSTRAINT wf_community_worldstate_operation_type_record_source_check CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_worldstate_operation_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_worldstate_operation_type IS 'WorldState 活动类型代码解释';


--
-- Name: COLUMN wf_community_worldstate_operation_type.description; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_operation_type.description IS '活动类型的业务说明';


--
-- Name: wf_community_worldstate_operation_type_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_worldstate_operation_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_worldstate_operation_type_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_worldstate_operation_type_id_seq OWNED BY nnmbot.wf_community_worldstate_operation_type.id;


--
-- Name: wf_community_worldstate_persistent_enemy; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_worldstate_persistent_enemy (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    locale character varying(16) NOT NULL,
    display_name character varying(500) NOT NULL,
    enemy_type character varying(200) DEFAULT ''::character varying NOT NULL,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text CONSTRAINT wf_community_worldstate_persistent__manual_fields_json_not_null NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false CONSTRAINT wf_community_worldstate_persistent_ene_manual_modified_not_null NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    CONSTRAINT wf_community_worldstate_persistent_enemy_record_source_check CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_worldstate_persistent_enemy; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_worldstate_persistent_enemy IS 'WorldState 持续敌人代码解释';


--
-- Name: COLUMN wf_community_worldstate_persistent_enemy.enemy_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_persistent_enemy.enemy_type IS '持续敌人的类型代码';


--
-- Name: wf_community_worldstate_persistent_enemy_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_worldstate_persistent_enemy_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_worldstate_persistent_enemy_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_worldstate_persistent_enemy_id_seq OWNED BY nnmbot.wf_community_worldstate_persistent_enemy.id;


--
-- Name: wf_community_worldstate_rule; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_worldstate_rule (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    dataset_code character varying(80) NOT NULL,
    locale character varying(8) NOT NULL,
    source_hash character varying(64) NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    upstream_hash character varying(64) DEFAULT repeat('0'::text, 64) NOT NULL,
    CONSTRAINT ck_wf_community_worldstate_rule_locale CHECK (((locale)::text = ANY (ARRAY[('en'::character varying)::text, ('zh'::character varying)::text]))),
    CONSTRAINT ck_wf_community_worldstate_rule_source CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_worldstate_rule; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_worldstate_rule IS 'WFCD 提供的 WorldState 静态规则和中英文规则数据';


--
-- Name: COLUMN wf_community_worldstate_rule.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_rule.record_key IS '规则稳定键';


--
-- Name: COLUMN wf_community_worldstate_rule.dataset_code; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_rule.dataset_code IS 'WFCD 静态数据集名称';


--
-- Name: COLUMN wf_community_worldstate_rule.locale; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_rule.locale IS '仅支持 en 和 zh，中文缺失时由业务回退英文';


--
-- Name: COLUMN wf_community_worldstate_rule.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_rule.source_hash IS '来源记录 SHA-256';


--
-- Name: wf_community_worldstate_rule_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_worldstate_rule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_worldstate_rule_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_worldstate_rule_id_seq OWNED BY nnmbot.wf_community_worldstate_rule.id;


--
-- Name: wf_community_worldstate_sortie; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_worldstate_sortie (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    locale character varying(16) NOT NULL,
    record_type character varying(16) NOT NULL,
    display_name character varying(500) NOT NULL,
    description character varying(2000) DEFAULT ''::character varying NOT NULL,
    faction_reference character varying(200) DEFAULT ''::character varying NOT NULL,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    CONSTRAINT wf_community_worldstate_sortie_record_source_check CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text]))),
    CONSTRAINT wf_community_worldstate_sortie_record_type_check CHECK (((record_type)::text = ANY (ARRAY[('MODIFIER'::character varying)::text, ('BOSS'::character varying)::text])))
);


--
-- Name: TABLE wf_community_worldstate_sortie; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_worldstate_sortie IS 'WorldState 突击静态代码簿，按记录类型区分修正和首领';


--
-- Name: COLUMN wf_community_worldstate_sortie.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.id IS '记录主键，新增预览预分配雪花 ID 并在采纳时沿用';


--
-- Name: COLUMN wf_community_worldstate_sortie.record_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.record_key IS '上游突击修正或首领代码，与类型和语言共同标识业务对象';


--
-- Name: COLUMN wf_community_worldstate_sortie.locale; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.locale IS '代码簿语言';


--
-- Name: COLUMN wf_community_worldstate_sortie.record_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.record_type IS '突击静态代码记录类型：MODIFIER 修正、BOSS 首领';


--
-- Name: COLUMN wf_community_worldstate_sortie.display_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.display_name IS '代码对应的展示名称';


--
-- Name: COLUMN wf_community_worldstate_sortie.description; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.description IS '突击修正说明，首领记录为空';


--
-- Name: COLUMN wf_community_worldstate_sortie.faction_reference; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.faction_reference IS '突击首领阵营代码，修正记录为空';


--
-- Name: COLUMN wf_community_worldstate_sortie.current_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.current_json IS '当前生效的完整结构化记录 JSON';


--
-- Name: COLUMN wf_community_worldstate_sortie.upstream_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.upstream_json IS '最近采纳的上游记录 JSON';


--
-- Name: COLUMN wf_community_worldstate_sortie.original_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.original_json IS '首次导入的原始记录 JSON';


--
-- Name: COLUMN wf_community_worldstate_sortie.manual_fields_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.manual_fields_json IS '旧协议兼容字段，不再参与社区字段合并';


--
-- Name: COLUMN wf_community_worldstate_sortie.record_source; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.record_source IS 'COMMUNITY 社区同步或 SELF_MAINTAINED 自维护';


--
-- Name: COLUMN wf_community_worldstate_sortie.manual_modified; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.manual_modified IS '旧协议兼容字段，社区采纳固定为 false';


--
-- Name: COLUMN wf_community_worldstate_sortie.upstream_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.upstream_hash IS '最近采纳上游内容摘要';


--
-- Name: COLUMN wf_community_worldstate_sortie.source_hash; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.source_hash IS '当前内容摘要';


--
-- Name: COLUMN wf_community_worldstate_sortie.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.enabled IS '是否供查询使用，社区移除执行物理删除';


--
-- Name: COLUMN wf_community_worldstate_sortie.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.created_at IS '记录创建时间';


--
-- Name: COLUMN wf_community_worldstate_sortie.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.updated_at IS '记录更新时间';


--
-- Name: COLUMN wf_community_worldstate_sortie.source_code; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.source_code IS '上游文件或自维护渠道的稳定来源代码';


--
-- Name: COLUMN wf_community_worldstate_sortie.last_imported_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_sortie.last_imported_at IS '最近人工采纳时间';


--
-- Name: wf_community_worldstate_sortie_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_worldstate_sortie_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_worldstate_sortie_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_worldstate_sortie_id_seq OWNED BY nnmbot.wf_community_worldstate_sortie.id;


--
-- Name: wf_community_worldstate_syndicate; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_worldstate_syndicate (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    locale character varying(16) NOT NULL,
    display_name character varying(500) NOT NULL,
    faction_reference character varying(200) DEFAULT ''::character varying NOT NULL,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    CONSTRAINT wf_community_worldstate_syndicate_record_source_check CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_worldstate_syndicate; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_worldstate_syndicate IS 'WorldState 集团代码解释';


--
-- Name: COLUMN wf_community_worldstate_syndicate.faction_reference; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_syndicate.faction_reference IS '集团关联阵营代码';


--
-- Name: wf_community_worldstate_syndicate_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_worldstate_syndicate_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_worldstate_syndicate_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_worldstate_syndicate_id_seq OWNED BY nnmbot.wf_community_worldstate_syndicate.id;


--
-- Name: wf_community_worldstate_upgrade_type; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_community_worldstate_upgrade_type (
    id bigint NOT NULL,
    record_key character varying(500) NOT NULL,
    locale character varying(16) NOT NULL,
    display_name character varying(500) NOT NULL,
    category character varying(200) DEFAULT ''::character varying NOT NULL,
    current_json text NOT NULL,
    upstream_json text NOT NULL,
    original_json text NOT NULL,
    manual_fields_json text DEFAULT '{}'::text CONSTRAINT wf_community_worldstate_upgrade_typ_manual_fields_json_not_null NOT NULL,
    record_source character varying(32) DEFAULT 'COMMUNITY'::character varying NOT NULL,
    manual_modified boolean DEFAULT false NOT NULL,
    upstream_hash character varying(64) NOT NULL,
    source_hash character varying(64) NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    source_code character varying(200),
    last_imported_at timestamp without time zone,
    CONSTRAINT wf_community_worldstate_upgrade_type_record_source_check CHECK (((record_source)::text = ANY (ARRAY[('COMMUNITY'::character varying)::text, ('SELF_MAINTAINED'::character varying)::text])))
);


--
-- Name: TABLE wf_community_worldstate_upgrade_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_community_worldstate_upgrade_type IS 'WorldState 升级类型代码解释';


--
-- Name: COLUMN wf_community_worldstate_upgrade_type.category; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_community_worldstate_upgrade_type.category IS '升级类型所属分类';


--
-- Name: wf_community_worldstate_upgrade_type_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_community_worldstate_upgrade_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_community_worldstate_upgrade_type_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_community_worldstate_upgrade_type_id_seq OWNED BY nnmbot.wf_community_worldstate_upgrade_type.id;


--
-- Name: wf_forum_article; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_forum_article (
    id bigint NOT NULL,
    slug character varying(160) NOT NULL,
    title character varying(300) NOT NULL,
    summary character varying(1000),
    tags character varying(1000),
    category character varying(120),
    cover_url character varying(1000),
    content_html text NOT NULL,
    content_markdown text,
    page_url character varying(1000) NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    sort_order integer DEFAULT 0 NOT NULL,
    created_by_user_id bigint NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    CONSTRAINT ck_wf_forum_article_sort_order CHECK ((sort_order >= 0))
);


--
-- Name: TABLE wf_forum_article; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_forum_article IS 'Warframe 论坛文章及配装内容';


--
-- Name: COLUMN wf_forum_article.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_forum_article.id IS '论坛文章雪花 ID';


--
-- Name: COLUMN wf_forum_article.slug; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_forum_article.slug IS '文章稳定业务标识和 URL 短标识';


--
-- Name: COLUMN wf_forum_article.title; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_forum_article.title IS '文章标题';


--
-- Name: COLUMN wf_forum_article.summary; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_forum_article.summary IS '文章摘要';


--
-- Name: COLUMN wf_forum_article.tags; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_forum_article.tags IS '文章标签，按管理端约定保存';


--
-- Name: COLUMN wf_forum_article.category; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_forum_article.category IS '文章分类';


--
-- Name: COLUMN wf_forum_article.cover_url; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_forum_article.cover_url IS '文章封面地址';


--
-- Name: COLUMN wf_forum_article.content_html; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_forum_article.content_html IS '文章 HTML 正文';


--
-- Name: COLUMN wf_forum_article.content_markdown; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_forum_article.content_markdown IS '文章 Markdown 原文，可为空';


--
-- Name: COLUMN wf_forum_article.page_url; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_forum_article.page_url IS '文章对外查看 URI，仅保存前端 hash 路径，完整地址由命令运行时拼接';


--
-- Name: COLUMN wf_forum_article.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_forum_article.enabled IS '是否允许在管理端和机器人查询结果中使用';


--
-- Name: COLUMN wf_forum_article.sort_order; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_forum_article.sort_order IS '文章展示和查询结果排序值，数值越小越靠前';


--
-- Name: COLUMN wf_forum_article.created_by_user_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_forum_article.created_by_user_id IS '文章创建人的系统用户主键，仅通过应用层逻辑关联';


--
-- Name: COLUMN wf_forum_article.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_forum_article.created_at IS '创建时间';


--
-- Name: COLUMN wf_forum_article.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_forum_article.updated_at IS '最后更新时间';


--
-- Name: wf_group_watch_rule; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_group_watch_rule (
    id bigint NOT NULL,
    group_id bigint NOT NULL,
    member_id bigint NOT NULL,
    watch_type character varying(80) NOT NULL,
    watch_key character varying(160) NOT NULL,
    watch_value character varying(500) NOT NULL,
    enabled boolean DEFAULT true NOT NULL,
    notify_enabled boolean DEFAULT true NOT NULL,
    last_match_key character varying(500),
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: TABLE wf_group_watch_rule; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_group_watch_rule IS 'Warframe 群组成员蹲任务规则';


--
-- Name: COLUMN wf_group_watch_rule.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_group_watch_rule.id IS '蹲任务规则主键';


--
-- Name: COLUMN wf_group_watch_rule.group_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_group_watch_rule.group_id IS '逻辑群组主键，使用应用层逻辑关联';


--
-- Name: COLUMN wf_group_watch_rule.member_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_group_watch_rule.member_id IS '群组成员主键，使用应用层逻辑关联';


--
-- Name: COLUMN wf_group_watch_rule.watch_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_group_watch_rule.watch_type IS '规则类型，如任务、节点、派系、奖励或裂隙';


--
-- Name: COLUMN wf_group_watch_rule.watch_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_group_watch_rule.watch_key IS '规则匹配字段';


--
-- Name: COLUMN wf_group_watch_rule.watch_value; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_group_watch_rule.watch_value IS '规则匹配值';


--
-- Name: COLUMN wf_group_watch_rule.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_group_watch_rule.enabled IS '是否启用该成员的蹲任务规则';


--
-- Name: COLUMN wf_group_watch_rule.notify_enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_group_watch_rule.notify_enabled IS '命中规则后是否发送群消息，属于蹲任务插件配置';


--
-- Name: COLUMN wf_group_watch_rule.last_match_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_group_watch_rule.last_match_key IS '上次已处理的稳定命中标识，用于避免重复通知';


--
-- Name: COLUMN wf_group_watch_rule.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_group_watch_rule.created_at IS '创建时间';


--
-- Name: COLUMN wf_group_watch_rule.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_group_watch_rule.updated_at IS '最后更新时间';


--
-- Name: wf_group_watch_rule_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_group_watch_rule_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_group_watch_rule_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_group_watch_rule_id_seq OWNED BY nnmbot.wf_group_watch_rule.id;


--
-- Name: wf_manual_item; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_manual_item (
    id bigint NOT NULL,
    source_key character varying(500) NOT NULL,
    official_name character varying(500) NOT NULL,
    localized_name character varying(500) NOT NULL,
    normalized_name character varying(500) NOT NULL,
    wiki_slug character varying(300),
    market_slug character varying(300),
    remark character varying(1000),
    raw_json text,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: TABLE wf_manual_item; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_manual_item IS '管理员维护的 Warframe 人工物品数据';


--
-- Name: COLUMN wf_manual_item.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_item.id IS '人工物品主键';


--
-- Name: COLUMN wf_manual_item.source_key; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_item.source_key IS '人工物品稳定业务标识';


--
-- Name: COLUMN wf_manual_item.official_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_item.official_name IS '人工维护的英文名称';


--
-- Name: COLUMN wf_manual_item.localized_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_item.localized_name IS '人工维护的本地化名称';


--
-- Name: COLUMN wf_manual_item.normalized_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_item.normalized_name IS '人工物品规范化名称';


--
-- Name: COLUMN wf_manual_item.wiki_slug; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_item.wiki_slug IS '灰机 Wiki 页面路径';


--
-- Name: COLUMN wf_manual_item.market_slug; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_item.market_slug IS 'Warframe Market slug';


--
-- Name: COLUMN wf_manual_item.remark; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_item.remark IS '管理员备注';


--
-- Name: COLUMN wf_manual_item.raw_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_item.raw_json IS '人工扩展 JSON';


--
-- Name: COLUMN wf_manual_item.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_item.enabled IS '是否参与运行时解析';


--
-- Name: COLUMN wf_manual_item.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_item.created_at IS '首次写入时间';


--
-- Name: COLUMN wf_manual_item.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_item.updated_at IS '最后更新时间';


--
-- Name: wf_manual_item_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_manual_item_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_manual_item_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_manual_item_id_seq OWNED BY nnmbot.wf_manual_item.id;


--
-- Name: wf_manual_translation; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_manual_translation (
    id bigint NOT NULL,
    item_id bigint NOT NULL,
    locale character varying(20) NOT NULL,
    translated_name character varying(500) NOT NULL,
    remark text,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone NOT NULL,
    updated_at timestamp without time zone NOT NULL
);


--
-- Name: TABLE wf_manual_translation; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_manual_translation IS '管理员维护的物品人工翻译';


--
-- Name: COLUMN wf_manual_translation.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_translation.id IS '人工翻译主键';


--
-- Name: COLUMN wf_manual_translation.item_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_translation.item_id IS '对应人工物品主键，使用应用层逻辑关联';


--
-- Name: COLUMN wf_manual_translation.locale; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_translation.locale IS '语言区域编码';


--
-- Name: COLUMN wf_manual_translation.translated_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_translation.translated_name IS '人工翻译名称';


--
-- Name: COLUMN wf_manual_translation.remark; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_translation.remark IS '管理员备注';


--
-- Name: COLUMN wf_manual_translation.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_translation.enabled IS '是否参与运行时解析';


--
-- Name: COLUMN wf_manual_translation.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_translation.created_at IS '首次写入时间';


--
-- Name: COLUMN wf_manual_translation.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_manual_translation.updated_at IS '最后更新时间';


--
-- Name: wf_manual_translation_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_manual_translation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_manual_translation_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_manual_translation_id_seq OWNED BY nnmbot.wf_manual_translation.id;


--
-- Name: wf_official_export_record; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_official_export_record (
    id bigint NOT NULL,
    unique_name character varying(500) NOT NULL,
    export_file character varying(260) NOT NULL,
    export_type character varying(120) NOT NULL,
    record_type character varying(160),
    official_name character varying(500),
    localized_name character varying(500),
    parent_name character varying(500),
    product_category character varying(160),
    exclude_from_codex boolean,
    description text,
    long_description text,
    wiki_slug character varying(300),
    market_slug character varying(300),
    raw_json text NOT NULL,
    localized_raw_json text,
    enabled boolean DEFAULT true NOT NULL,
    created_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL
);


--
-- Name: TABLE wf_official_export_record; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_official_export_record IS 'Warframe 官方 PublicExport 公共身份、名称及完整双语原始 JSON';


--
-- Name: COLUMN wf_official_export_record.id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.id IS '官方记录主键';


--
-- Name: COLUMN wf_official_export_record.unique_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.unique_name IS '官方 PublicExport uniqueName，与 export_type 共同构成业务身份';


--
-- Name: COLUMN wf_official_export_record.export_file; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.export_file IS '记录所属 PublicExport 文件名';


--
-- Name: COLUMN wf_official_export_record.export_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.export_type IS '官方导出类型，与 unique_name 共同构成业务身份';


--
-- Name: COLUMN wf_official_export_record.record_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.record_type IS 'JSON 记录的具体类型';


--
-- Name: COLUMN wf_official_export_record.official_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.official_name IS '官方英文名称';


--
-- Name: COLUMN wf_official_export_record.localized_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.localized_name IS '官方本地化名称';


--
-- Name: COLUMN wf_official_export_record.parent_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.parent_name IS '官方父级名称';


--
-- Name: COLUMN wf_official_export_record.product_category; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.product_category IS '官方产品分类';


--
-- Name: COLUMN wf_official_export_record.exclude_from_codex; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.exclude_from_codex IS '是否排除在 Codex 之外';


--
-- Name: COLUMN wf_official_export_record.description; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.description IS '官方短描述';


--
-- Name: COLUMN wf_official_export_record.long_description; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.long_description IS '官方长描述';


--
-- Name: COLUMN wf_official_export_record.wiki_slug; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.wiki_slug IS '灰机 Wiki 页面路径';


--
-- Name: COLUMN wf_official_export_record.market_slug; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.market_slug IS 'Warframe Market slug';


--
-- Name: COLUMN wf_official_export_record.raw_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.raw_json IS '官方英文原始 JSON';


--
-- Name: COLUMN wf_official_export_record.localized_raw_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.localized_raw_json IS '官方本地化原始 JSON';


--
-- Name: COLUMN wf_official_export_record.enabled; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.enabled IS '是否参与运行时解析';


--
-- Name: COLUMN wf_official_export_record.created_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.created_at IS '首次写入时间';


--
-- Name: COLUMN wf_official_export_record.updated_at; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_export_record.updated_at IS '最后更新时间';


--
-- Name: wf_official_export_record_id_seq; Type: SEQUENCE; Schema: nnmbot; Owner: -
--

CREATE SEQUENCE nnmbot.wf_official_export_record_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: wf_official_export_record_id_seq; Type: SEQUENCE OWNED BY; Schema: nnmbot; Owner: -
--

ALTER SEQUENCE nnmbot.wf_official_export_record_id_seq OWNED BY nnmbot.wf_official_export_record.id;


--
-- Name: wf_official_recipe; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_official_recipe (
    official_record_id bigint NOT NULL,
    result_type character varying(500),
    build_price integer,
    build_time integer,
    consume_on_use boolean,
    skip_build_time_price integer,
    num integer,
    prime_selling_price integer,
    always_available boolean,
    ingredients_json text,
    secret_ingredients_json text
);


--
-- Name: TABLE wf_official_recipe; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_official_recipe IS 'Warframe 官方蓝图和配方高频查询字段';


--
-- Name: COLUMN wf_official_recipe.official_record_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_recipe.official_record_id IS '官方基础记录主键，使用应用层逻辑关联';


--
-- Name: COLUMN wf_official_recipe.result_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_recipe.result_type IS '配方产物 uniqueName';


--
-- Name: COLUMN wf_official_recipe.build_price; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_recipe.build_price IS '配方建造价格';


--
-- Name: COLUMN wf_official_recipe.build_time; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_recipe.build_time IS '配方建造时间';


--
-- Name: COLUMN wf_official_recipe.consume_on_use; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_recipe.consume_on_use IS '配方是否使用后消耗';


--
-- Name: COLUMN wf_official_recipe.skip_build_time_price; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_recipe.skip_build_time_price IS '配方立即完成价格';


--
-- Name: COLUMN wf_official_recipe.num; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_recipe.num IS '配方产出数量';


--
-- Name: COLUMN wf_official_recipe.prime_selling_price; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_recipe.prime_selling_price IS 'Prime 物品出售价格';


--
-- Name: COLUMN wf_official_recipe.always_available; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_recipe.always_available IS '配方是否始终可用';


--
-- Name: COLUMN wf_official_recipe.ingredients_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_recipe.ingredients_json IS '配方材料 JSON';


--
-- Name: COLUMN wf_official_recipe.secret_ingredients_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_recipe.secret_ingredients_json IS '配方隐藏材料 JSON';


--
-- Name: wf_official_region; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_official_region (
    official_record_id bigint NOT NULL,
    system_name character varying(160),
    system_index integer,
    node_type integer,
    mastery_req integer,
    mission_index integer,
    faction_index integer,
    min_enemy_level integer,
    max_enemy_level integer
);


--
-- Name: TABLE wf_official_region; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_official_region IS 'Warframe 官方星图节点高频查询字段';


--
-- Name: COLUMN wf_official_region.official_record_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_region.official_record_id IS '官方基础记录主键，使用应用层逻辑关联';


--
-- Name: COLUMN wf_official_region.system_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_region.system_name IS '节点所属星球或星系名称';


--
-- Name: COLUMN wf_official_region.system_index; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_region.system_index IS '节点所属星系索引';


--
-- Name: COLUMN wf_official_region.node_type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_region.node_type IS '节点类型';


--
-- Name: COLUMN wf_official_region.mastery_req; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_region.mastery_req IS '节点段位要求';


--
-- Name: COLUMN wf_official_region.mission_index; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_region.mission_index IS '节点任务类型索引';


--
-- Name: COLUMN wf_official_region.faction_index; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_region.faction_index IS '节点敌对派系索引';


--
-- Name: COLUMN wf_official_region.min_enemy_level; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_region.min_enemy_level IS '节点最小敌人等级';


--
-- Name: COLUMN wf_official_region.max_enemy_level; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_region.max_enemy_level IS '节点最大敌人等级';


--
-- Name: wf_official_relic_arcane; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_official_relic_arcane (
    official_record_id bigint NOT NULL,
    type character varying(160),
    rarity character varying(120),
    level_stats_json text,
    relic_rewards_json text
);


--
-- Name: TABLE wf_official_relic_arcane; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_official_relic_arcane IS 'Warframe 官方遗物和奥术高频查询字段';


--
-- Name: COLUMN wf_official_relic_arcane.official_record_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_relic_arcane.official_record_id IS '官方基础记录主键，使用应用层逻辑关联';


--
-- Name: COLUMN wf_official_relic_arcane.type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_relic_arcane.type IS '遗物或奥术类型';


--
-- Name: COLUMN wf_official_relic_arcane.rarity; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_relic_arcane.rarity IS '遗物或奥术稀有度';


--
-- Name: COLUMN wf_official_relic_arcane.level_stats_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_relic_arcane.level_stats_json IS '奥术分级属性 JSON';


--
-- Name: COLUMN wf_official_relic_arcane.relic_rewards_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_relic_arcane.relic_rewards_json IS '遗物奖励池 JSON';


--
-- Name: wf_official_upgrade; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_official_upgrade (
    official_record_id bigint NOT NULL,
    base_drain integer,
    fusion_limit integer,
    compat_name character varying(300),
    type character varying(160),
    subtype character varying(160),
    polarity character varying(120),
    rarity character varying(120),
    is_utility boolean,
    is_augment boolean,
    is_exilus boolean,
    num_upgrades_in_set integer,
    mod_set character varying(300),
    mod_set_values_json text,
    available_challenges_json text,
    level_stats_json text,
    upgrade_entries_json text,
    tags_json text
);


--
-- Name: TABLE wf_official_upgrade; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_official_upgrade IS 'Warframe 官方 MOD 和升级项高频查询字段';


--
-- Name: COLUMN wf_official_upgrade.official_record_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.official_record_id IS '官方基础记录主键，使用应用层逻辑关联';


--
-- Name: COLUMN wf_official_upgrade.base_drain; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.base_drain IS 'MOD 基础容量消耗';


--
-- Name: COLUMN wf_official_upgrade.fusion_limit; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.fusion_limit IS 'MOD 融合等级上限';


--
-- Name: COLUMN wf_official_upgrade.compat_name; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.compat_name IS 'MOD 兼容对象名称';


--
-- Name: COLUMN wf_official_upgrade.type; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.type IS '升级项类型';


--
-- Name: COLUMN wf_official_upgrade.subtype; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.subtype IS '升级项子类型';


--
-- Name: COLUMN wf_official_upgrade.polarity; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.polarity IS 'MOD 极性';


--
-- Name: COLUMN wf_official_upgrade.rarity; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.rarity IS 'MOD 稀有度';


--
-- Name: COLUMN wf_official_upgrade.is_utility; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.is_utility IS '是否为功能槽 MOD';


--
-- Name: COLUMN wf_official_upgrade.is_augment; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.is_augment IS '是否为强化 MOD';


--
-- Name: COLUMN wf_official_upgrade.is_exilus; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.is_exilus IS '是否为特殊槽 MOD';


--
-- Name: COLUMN wf_official_upgrade.num_upgrades_in_set; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.num_upgrades_in_set IS '升级套装包含数量';


--
-- Name: COLUMN wf_official_upgrade.mod_set; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.mod_set IS 'MOD 套装名称';


--
-- Name: COLUMN wf_official_upgrade.mod_set_values_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.mod_set_values_json IS 'MOD 套装数值 JSON';


--
-- Name: COLUMN wf_official_upgrade.available_challenges_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.available_challenges_json IS '可用挑战条件 JSON';


--
-- Name: COLUMN wf_official_upgrade.level_stats_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.level_stats_json IS '分级属性 JSON';


--
-- Name: COLUMN wf_official_upgrade.upgrade_entries_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.upgrade_entries_json IS '升级条目 JSON';


--
-- Name: COLUMN wf_official_upgrade.tags_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_upgrade.tags_json IS '升级项标签 JSON';


--
-- Name: wf_official_warframe; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_official_warframe (
    official_record_id bigint NOT NULL,
    health integer,
    shield integer,
    armor integer,
    stamina integer,
    power integer,
    mastery_req integer,
    sprint_speed double precision,
    codex_secret boolean,
    abilities_json text,
    exalted_json text,
    stats_json text,
    tags_json text
);


--
-- Name: TABLE wf_official_warframe; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_official_warframe IS 'Warframe 官方战甲高频查询字段';


--
-- Name: COLUMN wf_official_warframe.official_record_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_warframe.official_record_id IS '官方基础记录主键，使用应用层逻辑关联';


--
-- Name: COLUMN wf_official_warframe.health; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_warframe.health IS '战甲生命值';


--
-- Name: COLUMN wf_official_warframe.shield; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_warframe.shield IS '战甲护盾值';


--
-- Name: COLUMN wf_official_warframe.armor; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_warframe.armor IS '战甲护甲值';


--
-- Name: COLUMN wf_official_warframe.stamina; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_warframe.stamina IS '战甲体力值';


--
-- Name: COLUMN wf_official_warframe.power; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_warframe.power IS '战甲能量值';


--
-- Name: COLUMN wf_official_warframe.mastery_req; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_warframe.mastery_req IS '战甲段位要求';


--
-- Name: COLUMN wf_official_warframe.sprint_speed; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_warframe.sprint_speed IS '战甲冲刺速度';


--
-- Name: COLUMN wf_official_warframe.codex_secret; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_warframe.codex_secret IS '是否为 Codex 隐藏记录';


--
-- Name: COLUMN wf_official_warframe.abilities_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_warframe.abilities_json IS '战甲技能数组 JSON';


--
-- Name: COLUMN wf_official_warframe.exalted_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_warframe.exalted_json IS '战甲显赫武器数据 JSON';


--
-- Name: COLUMN wf_official_warframe.stats_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_warframe.stats_json IS '战甲扩展属性 JSON';


--
-- Name: COLUMN wf_official_warframe.tags_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_warframe.tags_json IS '战甲标签 JSON';


--
-- Name: wf_official_weapon; Type: TABLE; Schema: nnmbot; Owner: -
--

CREATE TABLE nnmbot.wf_official_weapon (
    official_record_id bigint NOT NULL,
    mastery_req integer,
    total_damage double precision,
    damage_per_shot_json text,
    critical_chance double precision,
    critical_multiplier double precision,
    proc_chance double precision,
    fire_rate double precision,
    accuracy double precision,
    magazine_size integer,
    reload_time double precision,
    multishot double precision,
    slot integer,
    noise character varying(120),
    trigger character varying(120),
    omega_attenuation double precision,
    range_json text,
    slam_attack double precision,
    slide_attack double precision,
    wind_up double precision,
    level_stats_json text,
    ingredients_json text,
    tags_json text
);


--
-- Name: TABLE wf_official_weapon; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON TABLE nnmbot.wf_official_weapon IS 'Warframe 官方武器高频查询字段';


--
-- Name: COLUMN wf_official_weapon.official_record_id; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.official_record_id IS '官方基础记录主键，使用应用层逻辑关联';


--
-- Name: COLUMN wf_official_weapon.mastery_req; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.mastery_req IS '武器段位要求';


--
-- Name: COLUMN wf_official_weapon.total_damage; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.total_damage IS '武器总伤害';


--
-- Name: COLUMN wf_official_weapon.damage_per_shot_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.damage_per_shot_json IS '武器分段伤害 JSON';


--
-- Name: COLUMN wf_official_weapon.critical_chance; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.critical_chance IS '武器暴击几率';


--
-- Name: COLUMN wf_official_weapon.critical_multiplier; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.critical_multiplier IS '武器暴击倍率';


--
-- Name: COLUMN wf_official_weapon.proc_chance; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.proc_chance IS '武器触发几率';


--
-- Name: COLUMN wf_official_weapon.fire_rate; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.fire_rate IS '武器射速';


--
-- Name: COLUMN wf_official_weapon.accuracy; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.accuracy IS '武器精准度';


--
-- Name: COLUMN wf_official_weapon.magazine_size; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.magazine_size IS '武器弹匣容量';


--
-- Name: COLUMN wf_official_weapon.reload_time; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.reload_time IS '武器换弹时间';


--
-- Name: COLUMN wf_official_weapon.multishot; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.multishot IS '武器多重射击';


--
-- Name: COLUMN wf_official_weapon.slot; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.slot IS '武器装备槽位';


--
-- Name: COLUMN wf_official_weapon.noise; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.noise IS '武器噪声类型';


--
-- Name: COLUMN wf_official_weapon.trigger; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.trigger IS '武器扳机类型';


--
-- Name: COLUMN wf_official_weapon.omega_attenuation; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.omega_attenuation IS '武器 Omega 衰减系数';


--
-- Name: COLUMN wf_official_weapon.range_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.range_json IS '武器射程结构 JSON';


--
-- Name: COLUMN wf_official_weapon.slam_attack; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.slam_attack IS '武器震地攻击数值';


--
-- Name: COLUMN wf_official_weapon.slide_attack; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.slide_attack IS '武器滑行攻击数值';


--
-- Name: COLUMN wf_official_weapon.wind_up; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.wind_up IS '武器蓄力时间';


--
-- Name: COLUMN wf_official_weapon.level_stats_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.level_stats_json IS '武器等级属性 JSON';


--
-- Name: COLUMN wf_official_weapon.ingredients_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.ingredients_json IS '武器制造材料 JSON';


--
-- Name: COLUMN wf_official_weapon.tags_json; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON COLUMN nnmbot.wf_official_weapon.tags_json IS '武器标签 JSON';


--
-- Name: alias id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.alias ALTER COLUMN id SET DEFAULT nextval('nnmbot.alias_id_seq'::regclass);


--
-- Name: bot_admin id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_admin ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_admin_id_seq'::regclass);


--
-- Name: bot_channel_instance id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_channel_instance ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_channel_instance_id_seq'::regclass);


--
-- Name: bot_command id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_command_id_seq'::regclass);


--
-- Name: bot_command_alias id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_alias ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_command_alias_id_seq'::regclass);


--
-- Name: bot_command_channel id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_channel ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_command_channel_id_seq'::regclass);


--
-- Name: bot_command_cost id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_cost ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_command_cost_id_seq'::regclass);


--
-- Name: bot_command_robot_role id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_robot_role ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_command_robot_role_id_seq'::regclass);


--
-- Name: bot_command_role id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_role ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_command_role_id_seq'::regclass);


--
-- Name: bot_command_template id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_template ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_command_template_id_seq'::regclass);


--
-- Name: bot_group id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_group ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_group_id_seq'::regclass);


--
-- Name: bot_group_bot id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_group_bot ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_group_bot_id_seq'::regclass);


--
-- Name: bot_group_member id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_group_member ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_group_member_id_seq'::regclass);


--
-- Name: bot_group_point_transaction id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_group_point_transaction ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_group_point_transaction_id_seq'::regclass);


--
-- Name: bot_robot_role id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_robot_role ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_robot_role_id_seq'::regclass);


--
-- Name: bot_robot_role_instance id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_robot_role_instance ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_robot_role_instance_id_seq'::regclass);


--
-- Name: bot_scope id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_scope ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_scope_id_seq'::regclass);


--
-- Name: bot_scope_member id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_scope_member ALTER COLUMN id SET DEFAULT nextval('nnmbot.bot_scope_member_id_seq'::regclass);


--
-- Name: mission_subscribe id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.mission_subscribe ALTER COLUMN id SET DEFAULT nextval('nnmbot.mission_subscribe_id_seq'::regclass);


--
-- Name: notification_history id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.notification_history ALTER COLUMN id SET DEFAULT nextval('nnmbot.notification_history_id_seq'::regclass);


--
-- Name: sys_menu id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_menu ALTER COLUMN id SET DEFAULT nextval('nnmbot.sys_menu_id_seq'::regclass);


--
-- Name: sys_operation_log id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_operation_log ALTER COLUMN id SET DEFAULT nextval('nnmbot.sys_operation_log_id_seq'::regclass);


--
-- Name: sys_permission id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_permission ALTER COLUMN id SET DEFAULT nextval('nnmbot.sys_permission_id_seq'::regclass);


--
-- Name: sys_role id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_role ALTER COLUMN id SET DEFAULT nextval('nnmbot.sys_role_id_seq'::regclass);


--
-- Name: sys_user id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_user ALTER COLUMN id SET DEFAULT nextval('nnmbot.sys_user_id_seq'::regclass);


--
-- Name: sys_user_identity id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_user_identity ALTER COLUMN id SET DEFAULT nextval('nnmbot.sys_user_identity_id_seq'::regclass);


--
-- Name: wf_community_arbitration_essence id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_arbitration_essence ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_arbitration_essence_id_seq'::regclass);


--
-- Name: wf_community_command_alias id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_command_alias ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_command_alias_id_seq'::regclass);


--
-- Name: wf_community_dataset_alias id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_alias ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_dataset_alias_id_seq'::regclass);


--
-- Name: wf_community_dataset_market_component id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_market_component ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_dataset_market_component_id_seq'::regclass);


--
-- Name: wf_community_dataset_market_element id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_market_element ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_dataset_market_element_id_seq'::regclass);


--
-- Name: wf_community_dataset_market_riven_tion id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_market_riven_tion ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_dataset_market_riven_tion_id_seq'::regclass);


--
-- Name: wf_community_dataset_market_riven_tion_alias id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_market_riven_tion_alias ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_dataset_market_riven_tion_alias_id_seq'::regclass);


--
-- Name: wf_community_dataset_nodes id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_nodes ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_dataset_nodes_id_seq'::regclass);


--
-- Name: wf_community_dataset_reward_pool id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_reward_pool ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_dataset_reward_pool_id_seq'::regclass);


--
-- Name: wf_community_dataset_riven_analyse_trend id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_riven_analyse_trend ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_dataset_riven_analyse_trend_id_seq'::regclass);


--
-- Name: wf_community_dataset_riven_trend id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_riven_trend ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_dataset_riven_trend_id_seq'::regclass);


--
-- Name: wf_community_dataset_state_translation id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_state_translation ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_dataset_state_translation_id_seq'::regclass);


--
-- Name: wf_community_dataset_translation id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_translation ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_dataset_translation_id_seq'::regclass);


--
-- Name: wf_community_fish_guide id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_fish_guide ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_fish_guide_id_seq'::regclass);


--
-- Name: wf_community_map_feature id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_map_feature ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_map_feature_id_seq'::regclass);


--
-- Name: wf_community_relic id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_relic ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_relic_id_seq'::regclass);


--
-- Name: wf_community_relic_drop id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_relic_drop ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_relic_drop_id_seq'::regclass);


--
-- Name: wf_community_relic_reward id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_relic_reward ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_relic_reward_id_seq'::regclass);


--
-- Name: wf_community_riven_unrollable id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_riven_unrollable ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_riven_unrollable_id_seq'::regclass);


--
-- Name: wf_community_term id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_term ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_term_id_seq'::regclass);


--
-- Name: wf_community_worldstate_faction id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_faction ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_worldstate_faction_id_seq'::regclass);


--
-- Name: wf_community_worldstate_fissure_modifier id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_fissure_modifier ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_worldstate_fissure_modifier_id_seq'::regclass);


--
-- Name: wf_community_worldstate_language id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_language ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_worldstate_language_id_seq'::regclass);


--
-- Name: wf_community_worldstate_mission_type id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_mission_type ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_worldstate_mission_type_id_seq'::regclass);


--
-- Name: wf_community_worldstate_node id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_node ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_worldstate_node_id_seq'::regclass);


--
-- Name: wf_community_worldstate_operation_type id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_operation_type ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_worldstate_operation_type_id_seq'::regclass);


--
-- Name: wf_community_worldstate_persistent_enemy id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_persistent_enemy ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_worldstate_persistent_enemy_id_seq'::regclass);


--
-- Name: wf_community_worldstate_rule id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_rule ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_worldstate_rule_id_seq'::regclass);


--
-- Name: wf_community_worldstate_sortie id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_sortie ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_worldstate_sortie_id_seq'::regclass);


--
-- Name: wf_community_worldstate_syndicate id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_syndicate ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_worldstate_syndicate_id_seq'::regclass);


--
-- Name: wf_community_worldstate_upgrade_type id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_upgrade_type ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_community_worldstate_upgrade_type_id_seq'::regclass);


--
-- Name: wf_group_watch_rule id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_group_watch_rule ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_group_watch_rule_id_seq'::regclass);


--
-- Name: wf_manual_item id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_manual_item ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_manual_item_id_seq'::regclass);


--
-- Name: wf_manual_translation id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_manual_translation ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_manual_translation_id_seq'::regclass);


--
-- Name: wf_official_export_record id; Type: DEFAULT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_official_export_record ALTER COLUMN id SET DEFAULT nextval('nnmbot.wf_official_export_record_id_seq'::regclass);


--
-- Data for Name: alias; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.alias (id, cn_name, en_name, alias_name) FROM stdin;
\.


--
-- Data for Name: bot_admin; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_admin (id, user_id, bot_id, created_at) FROM stdin;
\.


--
-- Data for Name: bot_channel_instance; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_channel_instance (id, platform, instance_id, display_name, bot_account_id, enabled, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: bot_command; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_command (id, code, name, game_code, description, enabled, show_in_help, output_mode, permission_override, group_role_required, cooldown_seconds, mention_invoker, created_at, updated_at) FROM stdin;
5	wiki	Wiki	warframe	返回 Warframe 物品对应的灰机 Wiki 链接	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
6	forum-loadout	wf配装	warframe	模糊查询 Warframe 论坛配装文章并返回文章地址	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
9	alerts	警报	warframe	查看当前警报	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
10	sorties	突击	warframe	查看当前突击	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
11	archon-hunts	执刑官猎杀	warframe	查看当前执刑官猎杀	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
12	void-trader	奸商	warframe	查看奸商行踪和商品	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
13	arbitration-table	仲裁表	warframe	查看仲裁轮换表	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
14	daily-deals	每日特惠	warframe	查看每日特惠	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
15	invasions	入侵	warframe	查看当前入侵	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
16	fissures	裂隙	warframe	查看普通裂隙	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
17	void-storms	九重天裂隙	warframe	查看九重天裂隙	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
18	steel-fissures	钢铁裂隙	warframe	查看钢铁路径裂隙	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
19	steel-path	钢铁奖励	warframe	查看钢铁路径奖励	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
20	cycles	平原	warframe	查看各个开放世界周期	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
21	ostrons	希图斯	warframe	查看希图斯赏金任务	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
22	entrati	英择谛	warframe	查看英择谛赏金任务	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
23	solaris	索拉里斯	warframe	查看索拉里斯联合赏金任务	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
25	nightwave	电波	warframe	查看电波任务	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
26	riven-disposition	紫卡倾向变动	warframe	查看紫卡倾向变动	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
27	translation	翻译	warframe	查询 Warframe 翻译	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
30	liches	赤毒	warframe	查询赤毒相关数据	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
31	sisters	信条	warframe	查询信条相关数据	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
32	perrin-sequence	佩兰数列	warframe	查询佩兰数列和信条武器	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
33	gold-trash	金垃圾	warframe	查询金垃圾推荐	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
34	silver-trash	银垃圾	warframe	查询银垃圾推荐	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
35	relics	遗物	warframe	查询遗物信息	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
36	riven-analyse	紫卡分析	warframe	分析紫卡截图或文本	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
37	calendar-1999	1999日历	warframe	查看 1999 日历	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
38	conclave	竞技场	warframe	查看当前 Conclave 挑战	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
39	subscribe	订阅	warframe	订阅 Warframe 事件通知	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
40	unsubscribe	取消订阅	warframe	取消 Warframe 事件通知订阅	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
46	watch-list	蹲任务	warframe	查看当前成员的 Warframe 蹲任务	t	t	TEXT	\N	\N	0	f	2026-09-29 00:00:00	2026-09-29 00:00:00
41	update-market-items	更新WM物品	warframe	更新 Warframe Market 物品数据	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
42	update-market-riven	更新WM紫卡	warframe	更新 Warframe Market 紫卡数据	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
43	update-sister	更新信条	warframe	更新信条和赤毒武器数据	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
44	update-translation	更新翻译	warframe	更新 Warframe 翻译数据	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
2	help	帮助	system	查看命令菜单	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-08-22 02:36:11.812122
3	sign-in	签到	system	每日签到并返回签到后的群组积分	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-08-23 16:07:54.491956
4	bind	绑定	system	绑定当前群组成员的 QQ 号	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-08-23 16:07:54.491956
8	arbitration	仲裁	warframe	查看当前仲裁任务	t	t	IMAGE	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-08-26 11:10:09.495834
24	duviri-cycle	双衍王境	warframe	查看双衍王境轮换	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-08-26 16:18:27.212129
45	jm	JM 下载	system	下载指定 JM 漫画并返回压缩 PDF	f	f	FILE	\N	\N	30	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
1	status	状态	system	查看机器人运行状态	t	t	IMAGE	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-08-23 10:38:54.042272
7	market	市场	warframe	查询 Warframe Market 实时订单	t	t	IMAGE	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
29	riven-market	紫卡市场查询	warframe	已合并至 market-riven；保留历史配置，不再注册独立插件	f	f	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
28	market-riven	紫卡市场	warframe	查询紫卡拍卖，支持正负词条和尾部页码，每页最多 10 条；zk/wr/wmr/rm 共用此命令	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
47	news	新闻	warframe	查看官方新闻与链接	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
48	flash-sales	促销	warframe	查看当前促销商品和价格	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
49	persistent-enemies	小小黑	warframe	查看持续敌人的血量和最近发现地点	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
50	syndicate-missions	集团任务	warframe	查看全部集团任务和赏金	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
51	construction-progress	舰队建造	warframe	查看巨人战舰和利刃豺狼建造进度	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
52	simaris	西玛里斯	warframe	查看公共研究目标，非玩家个人每日扫描任务	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
53	sentient-outposts	先遣舰	warframe	查看先遣舰当前节点	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
54	clan-initiative	氏族周任务	warframe	查看氏族每周奖励和积分门槛	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
55	circuit-normal	普通回环	warframe	查看当前普通回环战甲奖励选项	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
56	circuit-hard	灵化轮换	warframe	查看当前钢铁之路回环灵化奖励选项	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
57	calendar	实时日历	warframe	查看官方 KnownCalendarSeasons 日期排期	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
58	vault-trader	宝库商人	warframe	查看 Prime 宝库商人库存	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
59	timestamp	世界时间	warframe	查看当前世界状态快照时间	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
60	archimedeas	科研	warframe	查看全部科研任务	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
61	deep-archimedeas	深层科研	warframe	查看 CT_LAB 科研任务	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
62	hex-archimedeas	时光科研	warframe	查看 CT_HEX 科研任务	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
63	descents	深层下降	warframe	查看当前深层下降轮换	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
64	cavia	科维兽	warframe	查看科维兽赏金	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
65	holdfasts	坚守者	warframe	查看扎里曼坚守者赏金	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
66	hex-bounties	六人组赏金	warframe	查看六人组赏金	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
67	events	活动目标	warframe	查看官方限时活动目标	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
68	global-upgrades	全局增益	warframe	查看当前全局增益	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
69	today-arbitration	今日仲裁	warframe	按上海自然日查看已存档仲裁排期	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
70	efficient-arbitration	高效仲裁	warframe	按 NyxBot 星球、任务、阵营规则筛选仲裁排期	t	t	TEXT	\N	\N	0	f	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
\.


--
-- Data for Name: bot_command_alias; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_command_alias (id, command_id, alias_name, boundary_policy) FROM stdin;
1	32	信条武器	NONE
2	42	更新WM紫卡	NONE
3	31	/xt	WHITESPACE_REQUIRED
4	7	market	CJK_OR_WHITESPACE
5	38	竞技场	NONE
6	8	高价值	NONE
7	44	更新翻译	NONE
8	28	/wr	WHITESPACE_REQUIRED
9	9	警报	NONE
10	36	分析紫卡	NONE
11	20	福尔图娜	NONE
12	18	钢铁裂缝	NONE
13	38	conclave	WHITESPACE_REQUIRED
14	2	help	WHITESPACE_REQUIRED
15	2	HELP	WHITESPACE_REQUIRED
17	30	cd	WHITESPACE_REQUIRED
18	28	wr	WHITESPACE_REQUIRED
19	8	arbitration	WHITESPACE_REQUIRED
20	43	更新信条	NONE
21	22	火卫二赏金	NONE
22	21	地球赏金	NONE
23	7	订单	NONE
24	11	执行官	NONE
25	11	猎杀	NONE
26	20	夜灵平野	NONE
27	38	pvp	WHITESPACE_REQUIRED
28	8	仲裁任务	NONE
29	7	/wm	CJK_OR_WHITESPACE
30	7	/报价	NONE
31	37	1999	WHITESPACE_REQUIRED
32	10	突击	NONE
33	2	菜单	NONE
34	6	/wf配装	NONE
35	7	/订单	NONE
36	24	轮换	NONE
37	3	sign-in	WHITESPACE_REQUIRED
38	35	查核桃	NONE
39	6	wf配装	NONE
40	28	/wmr	WHITESPACE_REQUIRED
41	27	翻译	NONE
42	7	报价	NONE
43	11	执刑官	NONE
44	1	检查版本	NONE
45	14	特惠	NONE
46	17	九重天裂缝	NONE
47	1	版本	NONE
48	5	wiki	WHITESPACE_REQUIRED
50	20	夜灵平原	NONE
51	11	执政官	NONE
52	31	xt	WHITESPACE_REQUIRED
53	28	zk	WHITESPACE_REQUIRED
54	5	/wk	WHITESPACE_REQUIRED
55	28	wmr	WHITESPACE_REQUIRED
56	37	日历	NONE
57	16	裂缝	NONE
58	1	status	WHITESPACE_REQUIRED
59	17	九重天	NONE
60	35	核桃	NONE
61	23	金星赏金	NONE
62	41	更新WM物品	NONE
63	28	/zk	WHITESPACE_REQUIRED
64	2	指令	NONE
65	13	仲裁表	NONE
66	7	wm	CJK_OR_WHITESPACE
67	7	/市场	NONE
68	26	倾向变动	NONE
69	7	/market	CJK_OR_WHITESPACE
70	20	魔胎之境	NONE
71	2	命令	NONE
72	30	/赤毒	NONE
73	5	/wiki	WHITESPACE_REQUIRED
74	30	/cd	WHITESPACE_REQUIRED
75	1	运行状态	NONE
76	3	签到	NONE
77	4	bind	WHITESPACE_REQUIRED
78	31	/信条	NONE
79	5	wk	WHITESPACE_REQUIRED
80	4	绑定	NONE
81	32	佩兰	NONE
82	45	/jm	WHITESPACE_REQUIRED
83	45	jm	WHITESPACE_REQUIRED
84	28	/rm	WHITESPACE_REQUIRED
85	28	rm	WHITESPACE_REQUIRED
86	28	紫卡市场查询	WHITESPACE_REQUIRED
87	28	riven-market	WHITESPACE_REQUIRED
88	54	clanWeeklyInitiative	WHITESPACE_REQUIRED
89	56	钢铁回环	WHITESPACE_REQUIRED
90	51	constructionProgress	WHITESPACE_REQUIRED
91	54	氏族周任务	WHITESPACE_REQUIRED
92	47	news	WHITESPACE_REQUIRED
93	53	sentientOutposts	WHITESPACE_REQUIRED
94	60	科研	WHITESPACE_REQUIRED
95	68	global-upgrades	WHITESPACE_REQUIRED
96	62	hex-archimedeas	WHITESPACE_REQUIRED
97	61	deep-archimedeas	WHITESPACE_REQUIRED
98	66	hex-bounties	WHITESPACE_REQUIRED
99	55	circuit-normal	WHITESPACE_REQUIRED
100	47	新闻	WHITESPACE_REQUIRED
101	50	syndicate-missions	WHITESPACE_REQUIRED
102	52	simaris	WHITESPACE_REQUIRED
103	59	世界时间	WHITESPACE_REQUIRED
104	63	Descendia	WHITESPACE_REQUIRED
105	63	深层下降	WHITESPACE_REQUIRED
106	60	archimedeas	WHITESPACE_REQUIRED
107	49	persistent-enemies	WHITESPACE_REQUIRED
108	58	vault-trader	WHITESPACE_REQUIRED
109	56	duviriCycle:hard	WHITESPACE_REQUIRED
110	65	坚守者	WHITESPACE_REQUIRED
111	61	archimedeas:CT_LAB	WHITESPACE_REQUIRED
112	48	促销	WHITESPACE_REQUIRED
113	60	archimedea	WHITESPACE_REQUIRED
114	63	descents	WHITESPACE_REQUIRED
115	55	普通回廊	WHITESPACE_REQUIRED
116	65	holdfasts	WHITESPACE_REQUIRED
117	61	深层科研	WHITESPACE_REQUIRED
118	57	calendar	WHITESPACE_REQUIRED
119	52	西玛里斯	WHITESPACE_REQUIRED
120	54	clan-initiative	WHITESPACE_REQUIRED
121	64	科维兽	WHITESPACE_REQUIRED
122	48	flash-sales	WHITESPACE_REQUIRED
123	51	舰队建造	WHITESPACE_REQUIRED
124	62	archimedeas:CT_HEX	WHITESPACE_REQUIRED
125	51	construction-progress	WHITESPACE_REQUIRED
126	51	舰队	WHITESPACE_REQUIRED
127	62	时光科研	WHITESPACE_REQUIRED
128	64	cavia	WHITESPACE_REQUIRED
129	67	活动	WHITESPACE_REQUIRED
130	56	灵化轮换	WHITESPACE_REQUIRED
131	49	persistentEnemies	WHITESPACE_REQUIRED
132	68	globalUpgrades	WHITESPACE_REQUIRED
133	53	sentient-outposts	WHITESPACE_REQUIRED
134	56	灵化	WHITESPACE_REQUIRED
135	55	普通回环	WHITESPACE_REQUIRED
136	55	duviriCycle:normal	WHITESPACE_REQUIRED
137	65	扎里曼赏金	WHITESPACE_REQUIRED
138	64	科维兽赏金	WHITESPACE_REQUIRED
139	48	flashSales	WHITESPACE_REQUIRED
140	59	timestamp	WHITESPACE_REQUIRED
141	56	circuit-hard	WHITESPACE_REQUIRED
142	50	集团任务	WHITESPACE_REQUIRED
143	58	宝库商人	WHITESPACE_REQUIRED
144	58	vaultTrader	WHITESPACE_REQUIRED
145	57	实时日历	WHITESPACE_REQUIRED
146	66	六人组赏金	WHITESPACE_REQUIRED
147	67	活动目标	WHITESPACE_REQUIRED
148	67	events	WHITESPACE_REQUIRED
149	49	小小黑	WHITESPACE_REQUIRED
150	50	syndicateMissions	WHITESPACE_REQUIRED
151	68	全局增益	WHITESPACE_REQUIRED
152	63	descent	WHITESPACE_REQUIRED
153	53	先遣舰	WHITESPACE_REQUIRED
154	20	地球	WHITESPACE_REQUIRED
155	69	today-arbitration	WHITESPACE_REQUIRED
156	20	平原状态	WHITESPACE_REQUIRED
157	70	efficient-arbitration	WHITESPACE_REQUIRED
158	20	地球状态	WHITESPACE_REQUIRED
159	69	今日仲裁	WHITESPACE_REQUIRED
160	70	高效仲裁	WHITESPACE_REQUIRED
\.


--
-- Data for Name: bot_command_channel; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_command_channel (id, command_id, platform, output_mode) FROM stdin;
39	5	ONEBOT	TEXT
40	5	QQ_OFFICIAL	TEXT
41	6	ONEBOT	TEXT
42	6	QQ_OFFICIAL	TEXT
43	26	ONEBOT	TEXT
44	26	QQ_OFFICIAL	TEXT
45	27	ONEBOT	TEXT
46	27	QQ_OFFICIAL	TEXT
47	28	ONEBOT	TEXT
48	28	QQ_OFFICIAL	TEXT
49	29	ONEBOT	TEXT
50	29	QQ_OFFICIAL	TEXT
51	30	ONEBOT	TEXT
52	30	QQ_OFFICIAL	TEXT
53	31	ONEBOT	TEXT
54	31	QQ_OFFICIAL	TEXT
55	32	ONEBOT	TEXT
56	32	QQ_OFFICIAL	TEXT
57	33	ONEBOT	TEXT
58	33	QQ_OFFICIAL	TEXT
59	34	ONEBOT	TEXT
60	34	QQ_OFFICIAL	TEXT
61	35	ONEBOT	TEXT
62	35	QQ_OFFICIAL	TEXT
63	36	ONEBOT	TEXT
64	36	QQ_OFFICIAL	TEXT
65	37	ONEBOT	TEXT
66	37	QQ_OFFICIAL	TEXT
67	39	ONEBOT	TEXT
68	39	QQ_OFFICIAL	TEXT
69	40	ONEBOT	TEXT
70	40	QQ_OFFICIAL	TEXT
71	41	ONEBOT	TEXT
72	41	QQ_OFFICIAL	TEXT
73	42	ONEBOT	TEXT
74	42	QQ_OFFICIAL	TEXT
75	43	ONEBOT	TEXT
76	43	QQ_OFFICIAL	TEXT
77	44	ONEBOT	TEXT
78	44	QQ_OFFICIAL	TEXT
3	8	ONEBOT	IMAGE
1	7	ONEBOT	IMAGE
2	7	QQ_OFFICIAL	IMAGE
4	8	QQ_OFFICIAL	IMAGE
5	9	ONEBOT	IMAGE
6	9	QQ_OFFICIAL	IMAGE
7	10	ONEBOT	IMAGE
8	10	QQ_OFFICIAL	IMAGE
9	11	ONEBOT	IMAGE
10	11	QQ_OFFICIAL	IMAGE
11	12	ONEBOT	IMAGE
12	12	QQ_OFFICIAL	IMAGE
13	13	ONEBOT	IMAGE
14	13	QQ_OFFICIAL	IMAGE
15	14	ONEBOT	IMAGE
16	14	QQ_OFFICIAL	IMAGE
17	15	ONEBOT	IMAGE
18	15	QQ_OFFICIAL	IMAGE
19	16	ONEBOT	IMAGE
20	16	QQ_OFFICIAL	IMAGE
21	17	ONEBOT	IMAGE
22	17	QQ_OFFICIAL	IMAGE
23	18	ONEBOT	IMAGE
24	18	QQ_OFFICIAL	IMAGE
25	19	ONEBOT	IMAGE
26	19	QQ_OFFICIAL	IMAGE
27	20	ONEBOT	IMAGE
28	20	QQ_OFFICIAL	IMAGE
29	21	ONEBOT	IMAGE
30	21	QQ_OFFICIAL	IMAGE
31	22	ONEBOT	IMAGE
32	22	QQ_OFFICIAL	IMAGE
33	23	ONEBOT	IMAGE
34	23	QQ_OFFICIAL	IMAGE
35	24	ONEBOT	IMAGE
36	24	QQ_OFFICIAL	IMAGE
37	25	ONEBOT	IMAGE
38	25	QQ_OFFICIAL	IMAGE
79	45	ONEBOT	FILE
80	45	QQ_OFFICIAL	FILE
\.


--
-- Data for Name: bot_command_cost; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_command_cost (id, command_id, cost_points, enabled, reward_points, reward_enabled, description, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: bot_command_robot_role; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_command_robot_role (id, command_id, robot_role_id) FROM stdin;
1	5	1
2	6	1
3	7	1
4	8	1
5	9	1
6	10	1
7	11	1
8	12	1
9	13	1
10	14	1
11	15	1
12	16	1
13	17	1
14	18	1
15	19	1
16	20	1
17	21	1
18	22	1
19	23	1
20	24	1
21	25	1
22	26	1
23	27	1
24	28	1
25	29	1
26	30	1
27	31	1
28	32	1
29	33	1
30	34	1
31	35	1
32	36	1
33	37	1
34	38	1
35	39	1
36	40	1
37	41	1
38	42	1
39	43	1
40	44	1
81	47	1
82	48	1
83	49	1
84	50	1
85	51	1
86	52	1
87	53	1
88	54	1
89	55	1
90	56	1
91	57	1
92	58	1
93	59	1
94	60	1
95	61	1
96	62	1
97	63	1
98	64	1
99	65	1
100	66	1
101	67	1
102	68	1
103	69	1
104	70	1
\.


--
-- Data for Name: bot_command_role; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_command_role (id, command_id, role_id) FROM stdin;
\.


--
-- Data for Name: bot_command_template; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_command_template (id, template_code, name, command_id, sort_order, html_content, enabled, created_at, updated_at) FROM stdin;
2	warframe-world-state	Warframe 世界状态卡片	\N	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
4	warframe-world-state-arbitration	Warframe 世界状态卡片 - 仲裁	8	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
3	warframe-market-orders	Warframe Market 订单卡片	7	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0}.sub{color:#657386;font-size:15px;margin:8px 0 18px}\n.summary{display:grid;grid-template-columns:1fr 1fr;gap:12px;margin-bottom:18px}.price{background:#f4f8f6;border-radius:8px;padding:13px}\n.label{color:#657386;font-size:14px}.value{font-size:24px;font-weight:700;color:#13795b;margin-top:4px}\n.section{border-top:1px solid #e6ebf0;padding-top:14px;margin-top:14px}.section h2{font-size:19px;margin:0 0 10px}\n.order{display:flex;justify-content:space-between;border-bottom:1px solid #edf0f3;padding:9px 0;font-size:16px}\n.user{font-weight:600}.detail{color:#657386}.empty{color:#657386;font-size:16px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${itemName}">Warframe Market</h1>\n<div class="sub" th:text="${itemDescription}">实时订单</div>\n<div class="summary"><div class="price"><div class="label">最低卖价</div><div class="value" th:text="${lowestSellPrice}">暂无</div></div>\n<div class="price"><div class="label">最高买价</div><div class="value" th:text="${highestBuyPrice}">暂无</div></div></div>\n<div class="section"><h2>卖家（<span th:text="${sellerCount}">0</span>）</h2>\n  <div class="order" th:each="seller : ${sellers}"><span class="user" th:text="${seller.userName}">-</span><span class="detail"><span th:text="${seller.platinum}">0</span> 白金 × <span th:text="${seller.quantity}">0</span></span></div>\n  <div class="empty" th:if="${sellerCount == 0}">暂无卖家</div>\n</div>\n<div class="section"><h2>买家（<span th:text="${buyerCount}">0</span>）</h2>\n  <div class="order" th:each="buyer : ${buyers}"><span class="user" th:text="${buyer.userName}">-</span><span class="detail"><span th:text="${buyer.platinum}">0</span> 白金 × <span th:text="${buyer.quantity}">0</span></span></div>\n  <div class="empty" th:if="${buyerCount == 0}">暂无买家</div>\n</div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
5	warframe-world-state-alerts	Warframe 世界状态卡片 - 警报	9	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
6	warframe-world-state-sorties	Warframe 世界状态卡片 - 突击	10	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
7	warframe-world-state-archon-hunts	Warframe 世界状态卡片 - 执刑官猎杀	11	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
8	warframe-world-state-void-trader	Warframe 世界状态卡片 - 奸商	12	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
9	warframe-world-state-arbitration-table	Warframe 世界状态卡片 - 仲裁表	13	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
10	warframe-world-state-daily-deals	Warframe 世界状态卡片 - 每日特惠	14	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
11	warframe-world-state-invasions	Warframe 世界状态卡片 - 入侵	15	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
12	warframe-world-state-fissures	Warframe 世界状态卡片 - 裂隙	16	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
13	warframe-world-state-void-storms	Warframe 世界状态卡片 - 九重天裂隙	17	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
14	warframe-world-state-steel-fissures	Warframe 世界状态卡片 - 钢铁裂隙	18	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
15	warframe-world-state-steel-path	Warframe 世界状态卡片 - 钢铁奖励	19	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
16	warframe-world-state-cycles	Warframe 世界状态卡片 - 平原	20	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
17	warframe-world-state-ostrons	Warframe 世界状态卡片 - 希图斯	21	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
18	warframe-world-state-entrati	Warframe 世界状态卡片 - 英择谛	22	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
19	warframe-world-state-solaris	Warframe 世界状态卡片 - 索拉里斯	23	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
20	warframe-world-state-duviri-cycle	Warframe 世界状态卡片 - 双衍王境	24	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
21	warframe-world-state-nightwave	Warframe 世界状态卡片 - 电波	25	0	\n<!doctype html>\n<html lang="zh-CN">\n<head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f3f5f8;color:#182230;margin:0;padding:24px}\n.panel{width:760px;background:#fff;border:1px solid #d9e0e8;border-radius:10px;padding:24px;box-sizing:border-box}\nh1{font-size:30px;line-height:1.25;margin:0 0 8px}.meta{color:#657386;font-size:15px;margin-bottom:18px}\n.item{border-top:1px solid #e6ebf0;padding:13px 0}.entry{display:flex;gap:16px;line-height:1.55;font-size:17px}\n.key{min-width:150px;color:#657386;font-weight:600}.value{flex:1;word-break:break-word}\n.empty{color:#657386;font-size:17px;padding:14px 0}.foot{color:#8a97a6;font-size:13px;margin-top:18px}\n</style></head>\n<body><section class="panel">\n<h1 th:text="${title}">Warframe</h1>\n<div class="meta">数据条数：<span th:text="${itemCount}">0</span> · 来源：<span th:text="${source}">unknown</span></div>\n<div th:if="${available}">\n  <div class="item" th:each="item : ${items}">\n    <div class="entry" th:each="entry : ${item}">\n      <span class="key" th:text="${entry.key}">字段</span>\n      <span class="value" th:text="${entry.value}">值</span>\n    </div>\n  </div>\n</div>\n<div class="empty" th:unless="${available}">当前没有可用数据</div>\n<div class="foot">更新时间：<span th:text="${updatedAt}">-</span></div>\n</section></body></html>\n	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
23	market-orders	市场报价卡片	\N	0	<!doctype html><html><head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f5f7fb;color:#172033;margin:0;padding:28px}\n.panel{background:#fff;border:1px solid #dfe5ef;border-radius:12px;padding:24px}\nh1{font-size:28px;margin:0}.sub{color:#667085;margin:8px 0 20px}.price{font-size:24px;font-weight:700;color:#13795b}\n</style></head><body><div class="panel"><h1 th:text="${itemName}"></h1>\n<div class="sub" th:text="${itemDescription}"></div><div class="price" th:text="${lowestSellPrice}"></div>\n<div class="sub">参考中位价：<span th:text="${medianPrice}"></span></div></div></body></html>	t	2026-08-22 02:36:12.53981	2026-08-22 02:36:12.53981
1	status	运行状态卡片	1	0	<!doctype html><html><head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f5f7fb;color:#172033;margin:0;padding:28px}\n.panel{background:#fff;border:1px solid #dfe5ef;border-radius:12px;padding:24px}\nh1{font-size:28px;margin:0 0 20px}.grid{display:grid;grid-template-columns:1fr 1fr;gap:14px}\n.item{background:#f7f9fc;padding:14px;border-radius:8px}.label{color:#667085;font-size:13px}\n.value{font-size:20px;font-weight:700;margin-top:6px}\n</style></head><body><div class="panel"><h1>NnmBot 运行状态</h1>\n<div class="grid"><div class="item"><div class="label">机器人账号</div><div class="value" th:text="${accountId}"></div></div>\n<div class="item"><div class="label">会话类型</div><div class="value" th:text="${scope}"></div></div>\n<div class="item"><div class="label">运行时长</div><div class="value" th:text="${uptime}"></div></div>\n<div class="item"><div class="label">内存</div><div class="value" th:text="${memory}"></div></div></div>\n</div></body></html>	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
24	arbitrations	仲裁轮换卡片	8	0	<!doctype html><html><head><meta charset="UTF-8"><style>\nbody{font-family:"Noto Sans SC",sans-serif;background:#f5f7fb;color:#172033;margin:0;padding:28px}\nh1{font-size:28px;margin:0 0 18px}.item{padding:12px 0;border-bottom:1px solid #dfe5ef}\n.node{font-size:18px;font-weight:700}.meta,.notice{color:#475467;margin-top:8px;overflow-wrap:anywhere}\n</style></head><body><h1>仲裁排期</h1>\n<div class="notice" th:if="${notice}" th:text="${notice}"></div>\n<div th:if="${#lists.isEmpty(items)}">当前范围暂无已存档任务</div>\n<div class="item" th:each="item : ${items}"><div class="node" th:text="${item['node']}"></div>\n<div class="meta"><span th:text="${item['missionType']}"></span> / <span th:text="${item['faction']}"></span></div>\n<div class="meta"><span th:text="${item['activation']}"></span> - <span th:text="${item['expiry']}"></span></div>\n<div class="meta" th:if="${item['essence']}" th:text="${item['essence']}"></div></div>\n<div class="notice" th:if="${!#lists.isEmpty(unresolvedReferences)}">未解析数据：<span th:text="${unresolvedReferences}"></span></div>\n</body></html>	t	2026-08-22 02:36:12.53981	2026-09-28 15:57:12.652567
\.


--
-- Data for Name: bot_group; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_group (id, code, name, description, enabled, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: bot_group_bot; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_group_bot (id, group_id, bot_instance_id, platform, scope_type, scope_key, display_name, enabled) FROM stdin;
\.


--
-- Data for Name: bot_group_member; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_group_member (id, group_id, qq_id, member_openid, kook_id, qq_id_verified, qq_bound_at, display_name, member_role, enabled, points, total_earned, total_spent, version) FROM stdin;
\.


--
-- Data for Name: bot_group_point_transaction; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_group_point_transaction (id, group_id, member_id, command_code, transaction_type, amount, balance_after, idempotency_key, description, created_at) FROM stdin;
\.


--
-- Data for Name: bot_onebot_config; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_onebot_config (instance_id, connection_url, access_token, reconnect_interval_seconds) FROM stdin;
\.


--
-- Data for Name: bot_qq_official_config; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_qq_official_config (instance_id, app_id, client_secret, token_url, api_base_url, gateway_url, intents, gateway_connect_timeout_seconds, reconnect_delay_seconds) FROM stdin;
\.


--
-- Data for Name: bot_robot_role; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_robot_role (id, code, name, description, enabled, created_at, updated_at) FROM stdin;
1	1	默认机器人能力	默认业务命令能力角色	t	2026-08-23 10:24:09.19137	2026-08-23 10:24:09.19137
\.


--
-- Data for Name: bot_robot_role_instance; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_robot_role_instance (id, robot_role_id, bot_instance_id) FROM stdin;
\.


--
-- Data for Name: bot_scope; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_scope (id, platform, scope_type, scope_key, bot_instance_id, display_name, enabled) FROM stdin;
\.


--
-- Data for Name: bot_scope_member; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.bot_scope_member (id, scope_id, user_id, platform_user_id, member_role, enabled) FROM stdin;
\.


--
-- Data for Name: mission_subscribe; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.mission_subscribe (id, group_id, type, enabled, created_at) FROM stdin;
\.


--
-- Data for Name: notification_history; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.notification_history (id, event_type, event_id, group_id, sent_at) FROM stdin;
\.


--
-- Data for Name: sys_menu; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.sys_menu (id, parent_id, name, path, component, title, icon, sort_order, keep_alive, visible, enabled, fixed_tab, hide_tab, full_page, iframe, link, active_path, text_badge, created_at, updated_at) FROM stdin;
1	\N	Dashboard	/dashboard	/index/index	menus.dashboard.title	ri:pie-chart-line	10	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
2	\N	System	/system	/index/index	menus.system.title	ri:user-3-line	20	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
3	\N	SnowLumaConsole	/outside/iframe/snowluma	\N	SnowLuma	ri:snowflake-line	26	f	t	t	f	f	f	t	http://localhost:5098/	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
4	\N	QqWebLogin	/outside/iframe/qq-web-login	\N	QQ 网页登录	ri:qq-line	27	f	t	t	f	f	f	t	http://localhost:5899/	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
5	\N	BotManagement	/bot-management	/index/index	menus.bot.title	ri:robot-2-line	28	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
6	\N	Template	/template	/index/index	menus.template.title	ri:apps-2-line	30	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
7	\N	Widgets	/widgets	/index/index	menus.widgets.title	ri:apps-2-add-line	40	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
8	\N	Examples	/examples	/index/index	menus.examples.title	ri:sparkling-line	60	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
9	\N	Article	/article	/index/index	menus.article.title	ri:book-2-line	70	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
10	\N	Result	/result	/index/index	menus.result.title	ri:checkbox-circle-line	80	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
11	\N	Exception	/exception	/index/index	menus.exception.title	ri:error-warning-line	90	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
12	\N	Safeguard	/safeguard	/index/index	menus.safeguard.title	ri:shield-check-line	100	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
13	1	Console	console	/dashboard/console	menus.dashboard.console	ri:home-smile-2-line	11	t	t	t	t	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
14	1	Analysis	analysis	/dashboard/analysis	menus.dashboard.analysis	ri:align-item-bottom-line	12	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
15	1	Ecommerce	ecommerce	/dashboard/ecommerce	menus.dashboard.ecommerce	ri:bar-chart-box-line	13	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
16	2	User	user	/system/user	menus.system.user	ri:user-line	21	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
17	2	Role	role	/system/role	menus.system.role	ri:user-settings-line	22	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
18	2	UserCenter	user-center	/system/user-center	menus.system.userCenter	ri:user-line	23	t	f	t	f	t	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
19	2	Menus	menu	/system/menu	menus.system.menu	ri:menu-line	24	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
20	2	OperationLog	audit	/system/audit	menus.system.audit	ri:file-list-3-line	25	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
21	2	OnlineSession	session	/system/session	menus.system.session	ri:computer-line	26	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
22	2	Nested	nested	\N	menus.system.nested	ri:menu-unfold-3-line	27	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
23	5	BotInstanceManagement	instance	/bot/instance	menus.bot.instance	ri:robot-2-line	281	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
24	5	BotFunctionManagement	function	/bot/function	menus.bot.function	ri:settings-3-line	282	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
25	5	BotClusterManagement	cluster	/bot/cluster	menus.bot.cluster	ri:server-line	283	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
26	6	Cards	cards	/template/cards	menus.template.cards	ri:wallet-line	31	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
27	6	Banners	banners	/template/banners	menus.template.banners	ri:rectangle-line	32	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
28	6	Charts	charts	/template/charts	menus.template.charts	ri:bar-chart-box-line	33	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
29	6	Map	map	/template/map	menus.template.map	ri:map-pin-line	34	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
30	6	Chat	chat	/template/chat	menus.template.chat	ri:message-3-line	35	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
31	6	Calendar	calendar	/template/calendar	menus.template.calendar	ri:calendar-2-line	36	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
32	6	Pricing	pricing	/template/pricing	menus.template.pricing	ri:money-cny-box-line	37	t	t	t	f	f	t	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
33	7	Icon	icon	/widgets/icon	menus.widgets.icon	ri:palette-line	41	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
34	7	ImageCrop	image-crop	/widgets/image-crop	menus.widgets.imageCrop	ri:screenshot-line	42	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
35	7	Excel	excel	/widgets/excel	menus.widgets.excel	ri:download-2-line	43	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
36	7	Video	video	/widgets/video	menus.widgets.video	ri:vidicon-line	44	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
37	7	CountTo	count-to	/widgets/count-to	menus.widgets.countTo	ri:anthropic-line	45	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
38	7	WangEditor	wang-editor	/widgets/wang-editor	menus.widgets.wangEditor	ri:t-box-line	46	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
39	7	Watermark	watermark	/widgets/watermark	menus.widgets.watermark	ri:water-flash-line	47	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
40	7	ContextMenu	context-menu	/widgets/context-menu	menus.widgets.contextMenu	ri:menu-2-line	48	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
41	7	Qrcode	qrcode	/widgets/qrcode	menus.widgets.qrcode	ri:qr-code-line	49	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
42	7	Drag	drag	/widgets/drag	menus.widgets.drag	ri:drag-move-fill	50	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
43	7	TextScroll	text-scroll	/widgets/text-scroll	menus.widgets.textScroll	ri:input-method-line	51	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
44	7	Fireworks	fireworks	/widgets/fireworks	menus.widgets.fireworks	ri:magic-line	52	t	t	t	f	f	f	f	\N	\N	Hot	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
45	7	ElementUI	/outside/iframe/elementui	\N	menus.widgets.elementUI	ri:apps-2-line	53	f	t	t	f	f	f	t	https://element-plus.org/zh-CN/component/overview.html	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
46	8	Permission	permission	\N	menus.examples.permission.title	ri:fingerprint-line	61	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
47	8	Tabs	tabs	/examples/tabs	menus.examples.tabs	ri:price-tag-line	62	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
48	8	TablesBasic	tables/basic	/examples/tables/basic	menus.examples.tablesBasic	ri:layout-grid-line	63	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
49	8	Tables	tables	/examples/tables	menus.examples.tables	ri:table-3	64	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
50	8	Forms	forms	/examples/forms	menus.examples.forms	ri:table-view	65	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
51	8	SearchBar	form/search-bar	/examples/forms/search-bar	menus.examples.searchBar	ri:table-line	66	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
52	8	TablesTree	tables/tree	/examples/tables/tree	menus.examples.tablesTree	ri:layout-2-line	67	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
53	8	SocketChat	socket-chat	/examples/socket-chat	menus.examples.socketChat	ri:shake-hands-line	68	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
54	9	ArticleList	article-list	/article/list	menus.article.articleList	ri:article-line	71	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
55	9	ArticleDetail	detail/:id	/article/detail	menus.article.articleDetail	\N	72	t	f	t	f	t	f	f	\N	/article/article-list	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
56	9	ArticleComment	comment	/article/comment	menus.article.comment	ri:mail-line	73	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
57	9	ArticlePublish	publish	/article/publish	menus.article.articlePublish	ri:telegram-2-line	74	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
58	10	ResultSuccess	success	/result/success	menus.result.success	ri:checkbox-circle-line	81	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
59	10	ResultFail	fail	/result/fail	menus.result.fail	ri:close-circle-line	82	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
60	11	Exception403	403	/exception/403	menus.exception.forbidden	\N	91	t	t	t	f	t	t	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
61	11	Exception404	404	/exception/404	menus.exception.notFound	\N	92	t	t	t	f	t	t	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
62	11	Exception500	500	/exception/500	menus.exception.serverError	\N	93	t	t	t	f	t	t	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
63	12	SafeguardServer	server	/safeguard/server	menus.safeguard.server	ri:hard-drive-3-line	101	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
64	22	NestedMenu3	menu3	\N	menus.system.menu3	ri:align-justify	273	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
65	22	NestedMenu2	menu2	\N	menus.system.menu2	ri:align-justify	272	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
66	22	NestedMenu1	menu1	/system/nested/menu1	menus.system.menu1	ri:align-justify	271	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
67	46	PermissionPageVisibility	page-visibility	/examples/permission/page-visibility	menus.examples.permission.pageVisibility	ri:user-3-line	613	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
68	46	PermissionButtonAuth	button-auth	/examples/permission/button-auth	menus.examples.permission.buttonAuth	ri:mouse-line	612	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
69	46	PermissionSwitchRole	switch-role	/examples/permission/switch-role	menus.examples.permission.switchRole	ri:contacts-line	611	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
70	64	NestedMenu3-2	menu3-2	\N	menus.system.menu32	\N	2732	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
71	64	NestedMenu3-1	menu3-1	/system/nested/menu3	menus.system.menu31	\N	2731	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
72	65	NestedMenu2-1	menu2-1	/system/nested/menu2	menus.system.menu21	ri:align-justify	2721	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
73	70	NestedMenu3-2-1	menu3-2-1	/system/nested/menu3/menu3-2	menus.system.menu321	\N	27321	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
74	5	BotGroupManagement	group	/bot/group	menus.bot.group	ri:group-line	284	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
75	5	BotCapabilityManagement	capability	/bot/robot-role	menus.bot.capability	ri:shield-user-line	283	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
76	5	BotCommandManagement	command	\N	menus.bot.command.title	ri:terminal-box-line	282	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
77	76	BotTemplateManagement	template	/bot/template	menus.bot.command.template	ri:html5-line	2823	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
78	76	BotWarframeCommands	warframe	/bot/command	menus.bot.command.warframe	ri:game-line	2822	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
79	76	BotSystemCommands	system	/bot/command	menus.bot.command.system	ri:settings-3-line	2821	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
80	\N	Warframe	/warframe	/index/index	menus.warframe.title	ri:game-line	29	f	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
81	80	WarframeForum	forum	/warframe/forum	menus.warframe.forum	ri:book-open-line	294	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
82	80	WarframeCommunityData	community	/warframe/community-datasets	menus.warframe.community	ri:git-branch-line	293	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
83	80	WarframeWorldState	world-state	/warframe/world-state	menus.warframe.worldState	ri:earth-line	290	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
84	80	WarframeData	data	/warframe/data	menus.warframe.data	ri:database-2-line	292	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
85	80	WarframeSupplementalData	supplemental	/warframe/supplemental-data	menus.warframe.supplemental	ri:database-2-line	291	t	t	t	f	f	f	f	\N	\N	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
86	80	WarframeForumPublish	forum/publish	/warframe/forum/publish	menus.warframe.forumPublish	\N	2942	f	f	t	f	t	f	f	\N	/warframe/forum	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
87	80	WarframeForumManageDetail	forum/manage-detail/:id	/warframe/forum/detail	menus.warframe.forumDetail	\N	2941	f	f	t	f	t	f	f	\N	/warframe/forum	\N	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
\.


--
-- Data for Name: sys_operation_log; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.sys_operation_log (id, operator_id, operator_name, action, target_type, target_id, detail, ip_address, created_at) FROM stdin;
\.


--
-- Data for Name: sys_permission; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.sys_permission (id, code, name, type, menu_id, enabled, created_at, updated_at) FROM stdin;
1	system:user:view	查看用户	BUTTON	16	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
2	system:user:create	创建用户	BUTTON	16	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
3	system:user:update	修改用户	BUTTON	16	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
4	system:user:disable	禁用用户	BUTTON	16	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
5	system:role:view	查看角色	BUTTON	17	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
6	system:role:create	创建角色	BUTTON	17	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
7	system:role:update	修改角色	BUTTON	17	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
8	system:role:delete	删除角色	BUTTON	17	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
9	system:role:assign	分配权限	BUTTON	17	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
10	system:menu:view	查看菜单	BUTTON	19	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
11	system:menu:create	创建菜单	BUTTON	19	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
12	system:menu:update	修改菜单	BUTTON	19	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
13	system:menu:delete	删除菜单	BUTTON	19	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
14	system:audit:view	查看操作日志	BUTTON	20	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
15	system:session:view	查看在线会话	BUTTON	21	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
16	system:session:kickout	强制下线	BUTTON	21	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
17	bot:instance:view	查看机器人实例	BUTTON	23	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
18	bot:instance:create	创建机器人实例	BUTTON	23	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
19	bot:instance:update	修改机器人实例	BUTTON	23	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
20	bot:instance:delete	删除机器人实例	BUTTON	23	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
21	bot:instance:operate	启动或停止机器人实例	BUTTON	23	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
22	bot:instance:probe	获取 OneBot 登录账号	BUTTON	23	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
23	add	新增	BUTTON	68	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
24	edit	编辑	BUTTON	68	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
25	delete	删除	BUTTON	68	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
26	export	导出	BUTTON	68	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
27	view	查看	BUTTON	68	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
28	publish	发布	BUTTON	68	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
29	config	配置	BUTTON	68	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
30	manage	管理	BUTTON	68	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
31	bot:group:delete	删除群组作用域	BUTTON	74	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
32	bot:group:update	修改群组配置	BUTTON	74	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
33	bot:group:view	查看群组管理	BUTTON	74	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
34	bot:command:update	修改命令配置	BUTTON	79	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
35	bot:command:view	查看命令配置	BUTTON	79	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
36	warframe:forum:delete	删除 Warframe 论坛文章	BUTTON	81	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
37	warframe:forum:update	修改 Warframe 论坛文章	BUTTON	81	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
38	warframe:forum:create	创建 Warframe 论坛文章	BUTTON	81	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
39	warframe:forum:view	查看 Warframe 论坛文章	BUTTON	81	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
40	warframe:data:update	兼容维护 Warframe 本地数据	BUTTON	82	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
45	warframe:world:refresh	刷新 Warframe 世界数据	BUTTON	83	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
46	warframe:world:view	查看 Warframe 世界数据	BUTTON	83	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
47	warframe:data:view	兼容查看 Warframe 本地数据	BUTTON	84	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
48	warframe:game:refresh	刷新 Warframe 官方游戏数据	BUTTON	84	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
49	warframe:game:view	查看 Warframe 官方游戏数据	BUTTON	84	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
44	warframe:community:view	查看 Warframe 社区数据	BUTTON	82	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
43	warframe:community:update	维护 Warframe 社区数据	BUTTON	82	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
42	warframe:community:sync	同步 Warframe 社区数据	BUTTON	82	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
41	warframe:community:apply	审批 Warframe 社区数据	BUTTON	82	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
54	warframe:community:delete	删除自维护 Warframe 社区数据	BUTTON	82	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
\.


--
-- Data for Name: sys_role; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.sys_role (id, code, name, description, enabled, authority_level, data_scope, created_at, updated_at) FROM stdin;
1	R_SUPER	超级管理员	拥有全部管理权限	t	100	ALL	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
2	R_ADMIN	管理员	负责日常用户管理	t	50	ALL	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
3	R_USER	只读用户	仅访问基础功能	t	0	SELF	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567
\.


--
-- Data for Name: sys_role_menu; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.sys_role_menu (role_id, menu_id) FROM stdin;
1	1
1	2
1	3
1	4
1	5
1	6
1	7
1	8
1	9
1	10
1	11
1	12
1	13
1	14
1	15
1	16
1	17
1	18
1	19
1	20
1	21
1	22
1	23
1	24
1	25
1	26
1	27
1	28
1	29
1	30
1	31
1	32
1	33
1	34
1	35
1	36
1	37
1	38
1	39
1	40
1	41
1	42
1	43
1	44
1	45
1	46
1	47
1	48
1	49
1	50
1	51
1	52
1	53
1	54
1	55
1	56
1	57
1	58
1	59
1	60
1	61
1	62
1	63
1	64
1	65
1	66
1	67
1	68
1	69
1	70
1	71
1	72
1	73
2	1
2	2
2	3
2	4
2	5
2	6
2	7
2	8
2	9
2	10
2	11
2	12
2	13
2	14
2	15
2	16
2	18
2	22
2	23
2	24
2	25
2	26
2	27
2	28
2	29
2	30
2	31
2	32
2	33
2	34
2	35
2	36
2	37
2	38
2	39
2	40
2	41
2	42
2	43
2	44
2	45
2	46
2	47
2	48
2	49
2	50
2	51
2	52
2	53
2	54
2	55
2	56
2	57
2	58
2	59
2	60
2	61
2	62
2	63
2	64
2	65
2	66
2	68
2	69
2	70
2	71
2	72
2	73
1	74
2	74
1	75
2	75
1	76
2	76
1	77
2	77
1	78
2	78
1	79
2	79
1	80
2	80
1	81
2	81
1	82
2	82
1	83
2	83
1	84
2	84
1	85
2	85
1	86
2	86
1	87
2	87
\.


--
-- Data for Name: sys_role_permission; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.sys_role_permission (role_id, permission_id) FROM stdin;
1	1
1	2
1	3
1	4
1	5
1	6
1	7
1	8
1	9
1	10
1	11
1	12
1	13
1	14
1	15
1	16
1	17
1	18
1	19
1	20
1	21
1	22
1	23
1	24
1	25
1	26
1	27
1	28
1	29
1	30
2	1
2	2
2	3
2	4
2	5
2	17
2	18
2	19
2	20
2	21
2	22
2	23
2	24
2	25
2	26
2	27
2	28
2	29
2	30
1	31
2	31
1	32
2	32
1	33
2	33
1	34
2	34
1	35
2	35
1	36
2	36
1	37
2	37
1	38
2	38
1	39
2	39
1	40
2	40
1	41
2	41
1	42
2	42
1	43
2	43
1	44
2	44
1	54
2	54
1	45
2	45
1	46
2	46
1	47
2	47
1	48
2	48
1	49
2	49
\.


--
-- Data for Name: sys_user; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.sys_user (id, username, password, nickname, phone, email, gender, avatar, enabled, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: sys_user_identity; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.sys_user_identity (id, user_id, platform, external_user_id) FROM stdin;
\.


--
-- Data for Name: sys_user_role; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.sys_user_role (user_id, role_id) FROM stdin;
\.


--
-- Data for Name: wf_arbitration_schedule; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_arbitration_schedule (activation, expiry, upstream_id, node, mission_type, enemy, planet, bounds_json, source_url, fetched_at) FROM stdin;
\.


--
-- Data for Name: wf_community_arbitration_essence; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_arbitration_essence (id, record_key, node_name, essence_amount, current_json, upstream_json, original_json, manual_fields_json, record_source, manual_modified, upstream_hash, source_hash, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_bounty_definition; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_bounty_definition (id, record_key, record_source, source_code, last_imported_at, current_json, upstream_json, original_json, manual_fields_json, upstream_hash, source_hash, manual_modified, enabled, created_at, updated_at, name_key, description_key, icon) FROM stdin;
\.


--
-- Data for Name: wf_community_bounty_stage; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_bounty_stage (definition_id, entry_order, stage_number, option_number, job_reference) FROM stdin;
\.


--
-- Data for Name: wf_community_command_alias; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_command_alias (id, record_key, alias_name, target_field, current_json, upstream_json, original_json, manual_fields_json, record_source, manual_modified, upstream_hash, source_hash, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_dataset_alias; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_dataset_alias (id, record_key, record_source, source_id, alias_name, target_name, current_json, upstream_json, original_json, manual_fields_json, upstream_hash, source_hash, manual_modified, enabled, created_at, updated_at, normalized_alias, manual_item_id, remark, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_dataset_market_component; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_dataset_market_component (id, record_key, record_source, input_alias, market_suffix, current_json, upstream_json, original_json, manual_fields_json, upstream_hash, source_hash, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
1	alias:机体蓝图	SELF_MAINTAINED	机体蓝图	chassis blueprint	{"input_alias":"机体蓝图","market_suffix":"chassis blueprint"}	{}	{"input_alias":"机体蓝图","market_suffix":"chassis blueprint"}	{}	0000000000000000000000000000000000000000000000000000000000000000	0000000000000000000000000000000000000000000000000000000000000000	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_component	\N
2	alias:机体	SELF_MAINTAINED	机体	chassis	{"input_alias":"机体","market_suffix":"chassis"}	{}	{"input_alias":"机体","market_suffix":"chassis"}	{}	0000000000000000000000000000000000000000000000000000000000000000	0000000000000000000000000000000000000000000000000000000000000000	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_component	\N
3	alias:系统蓝图	SELF_MAINTAINED	系统蓝图	systems blueprint	{"input_alias":"系统蓝图","market_suffix":"systems blueprint"}	{}	{"input_alias":"系统蓝图","market_suffix":"systems blueprint"}	{}	0000000000000000000000000000000000000000000000000000000000000000	0000000000000000000000000000000000000000000000000000000000000000	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_component	\N
4	alias:系统	SELF_MAINTAINED	系统	systems	{"input_alias":"系统","market_suffix":"systems"}	{}	{"input_alias":"系统","market_suffix":"systems"}	{}	0000000000000000000000000000000000000000000000000000000000000000	0000000000000000000000000000000000000000000000000000000000000000	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_component	\N
5	alias:头部神经光元蓝图	SELF_MAINTAINED	头部神经光元蓝图	neuroptics blueprint	{"input_alias":"头部神经光元蓝图","market_suffix":"neuroptics blueprint"}	{}	{"input_alias":"头部神经光元蓝图","market_suffix":"neuroptics blueprint"}	{}	0000000000000000000000000000000000000000000000000000000000000000	0000000000000000000000000000000000000000000000000000000000000000	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_component	\N
6	alias:头部神经光元	SELF_MAINTAINED	头部神经光元	neuroptics	{"input_alias":"头部神经光元","market_suffix":"neuroptics"}	{}	{"input_alias":"头部神经光元","market_suffix":"neuroptics"}	{}	0000000000000000000000000000000000000000000000000000000000000000	0000000000000000000000000000000000000000000000000000000000000000	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_component	\N
7	alias:头	SELF_MAINTAINED	头	neuroptics	{"input_alias":"头","market_suffix":"neuroptics"}	{}	{"input_alias":"头","market_suffix":"neuroptics"}	{}	0000000000000000000000000000000000000000000000000000000000000000	0000000000000000000000000000000000000000000000000000000000000000	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_component	\N
8	alias:枪机	SELF_MAINTAINED	枪机	receiver	{"input_alias":"枪机","market_suffix":"receiver"}	{}	{"input_alias":"枪机","market_suffix":"receiver"}	{}	0000000000000000000000000000000000000000000000000000000000000000	0000000000000000000000000000000000000000000000000000000000000000	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_component	\N
9	alias:枪管	SELF_MAINTAINED	枪管	barrel	{"input_alias":"枪管","market_suffix":"barrel"}	{}	{"input_alias":"枪管","market_suffix":"barrel"}	{}	0000000000000000000000000000000000000000000000000000000000000000	0000000000000000000000000000000000000000000000000000000000000000	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_component	\N
10	alias:枪托	SELF_MAINTAINED	枪托	stock	{"input_alias":"枪托","market_suffix":"stock"}	{}	{"input_alias":"枪托","market_suffix":"stock"}	{}	0000000000000000000000000000000000000000000000000000000000000000	0000000000000000000000000000000000000000000000000000000000000000	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_component	\N
11	alias:一套	SELF_MAINTAINED	一套	set	{"input_alias":"一套","market_suffix":"set"}	{}	{"input_alias":"一套","market_suffix":"set"}	{}	0000000000000000000000000000000000000000000000000000000000000000	0000000000000000000000000000000000000000000000000000000000000000	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_component	\N
12	alias:套装	SELF_MAINTAINED	套装	set	{"input_alias":"套装","market_suffix":"set"}	{}	{"input_alias":"套装","market_suffix":"set"}	{}	0000000000000000000000000000000000000000000000000000000000000000	0000000000000000000000000000000000000000000000000000000000000000	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_component	\N
13	alias:蓝图	SELF_MAINTAINED	蓝图	blueprint	{"input_alias":"蓝图","market_suffix":"blueprint"}	{}	{"input_alias":"蓝图","market_suffix":"blueprint"}	{}	0000000000000000000000000000000000000000000000000000000000000000	0000000000000000000000000000000000000000000000000000000000000000	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_component	\N
\.


--
-- Data for Name: wf_community_dataset_market_element; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_dataset_market_element (id, record_key, record_source, source_id, element_code, display_name, current_json, upstream_json, original_json, manual_fields_json, upstream_hash, source_hash, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_dataset_market_riven_tion; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_dataset_market_riven_tion (id, record_key, record_source, source_id, effect, attribute_group, negative_only, positive_is_negative, prefix, search_only, suffix, units, url_name, exclusive_to_json, current_json, upstream_json, original_json, manual_fields_json, upstream_hash, source_hash, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_dataset_market_riven_tion_alias; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_dataset_market_riven_tion_alias (id, record_key, record_source, source_id, attribute_code, alias_name, current_json, upstream_json, original_json, manual_fields_json, upstream_hash, source_hash, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
18	alias:暴击	SELF_MAINTAINED	\N	critical_chance	暴击	{"cn":"暴击","en":"critical_chance"}	{}	{"cn":"暴击","en":"critical_chance"}	{}	44136fa355b3678a1146ad16f7e8649e94fb4fc21fe77e8310c060f61caaff8a	06be0e5f7aa62fa4bf4d50dbcf875d094530c0218fb0cca23fa271d1428376ab	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_riven_tion_alias	\N
19	alias:基础伤害	SELF_MAINTAINED	\N	base_damage_/_melee_damage	基础伤害	{"cn":"基础伤害","en":"base_damage_/_melee_damage"}	{}	{"cn":"基础伤害","en":"基础伤害"}	{}	44136fa355b3678a1146ad16f7e8649e94fb4fc21fe77e8310c060f61caaff8a	ca24021f72db88f63fd5459ff6fc2b47bdd2f41801b0ca3e45d5016572d1e63c	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_riven_tion_alias	\N
20	alias:基双爆	SELF_MAINTAINED	\N	base_damage_/_melee_damage,critical_chance,critical_damage	基双爆	{"cn":"基双爆","en":"base_damage_/_melee_damage,critical_chance,critical_damage"}	{}	{"cn":"基双爆","en":"base_damage_/_melee_damage,critical_chance,critical_damage"}	{}	44136fa355b3678a1146ad16f7e8649e94fb4fc21fe77e8310c060f61caaff8a	6354cb676edde4b30296b81083b736cc59b90f1bacc0a8edfc4b60a6ead46e2f	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_riven_tion_alias	\N
21	alias:多双爆	SELF_MAINTAINED	\N	multishot,critical_chance,critical_damage	多双爆	{"cn":"多双爆","en":"multishot,critical_chance,critical_damage"}	{}	{"cn":"多双爆","en":"multishot,critical_chance,critical_damage"}	{}	44136fa355b3678a1146ad16f7e8649e94fb4fc21fe77e8310c060f61caaff8a	3f57a0b7d9a3bce652c5b59862efcc2cc00e41a23d38b4c7b7285fa9433418fa	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_riven_tion_alias	\N
22	alias:滑爆	SELF_MAINTAINED	\N	critical_chance_on_slide_attack	滑爆	{"cn":"滑爆","en":"critical_chance_on_slide_attack"}	{}	{"cn":"滑爆","en":"critical_chance_on_slide_attack"}	{}	44136fa355b3678a1146ad16f7e8649e94fb4fc21fe77e8310c060f61caaff8a	0c9f58c2499710d2ff47d1873300668ef5961586871e843324b4b5286bb7d6a4	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_riven_tion_alias	\N
23	alias:划爆	SELF_MAINTAINED	\N	critical_chance_on_slide_attack	划爆	{"cn":"划爆","en":"critical_chance_on_slide_attack"}	{}	{"cn":"划爆","en":"critical_chance_on_slide_attack"}	{}	44136fa355b3678a1146ad16f7e8649e94fb4fc21fe77e8310c060f61caaff8a	83014692d414099d8d9f1bd3e959b01af8b7ee30bd165b5d8e9f070e04435169	f	t	2026-09-28 15:57:12.652567	2026-09-28 15:57:12.652567	nnmbot:market_riven_tion_alias	\N
\.


--
-- Data for Name: wf_community_dataset_nodes; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_dataset_nodes (id, record_key, record_source, unique_name, display_name, system_index, system_name, node_type, mastery_req, mission_index, faction_index, min_enemy_level, max_enemy_level, current_json, upstream_json, original_json, manual_fields_json, upstream_hash, source_hash, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_dataset_reward_pool; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_dataset_reward_pool (id, record_key, record_source, unique_name, reward_count, rewards_json, current_json, upstream_json, original_json, manual_fields_json, upstream_hash, source_hash, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_dataset_riven_analyse_trend; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_dataset_riven_analyse_trend (id, record_key, record_source, source_id, display_name, prefix, suffix, archwing, melee_value, pistol, rifle, shotgun, current_json, upstream_json, original_json, manual_fields_json, upstream_hash, source_hash, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_dataset_riven_trend; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_dataset_riven_trend (id, record_key, record_source, source_id, trend_time, new_dot, new_num, old_dot, old_num, trend_name, weapon_type, current_json, upstream_json, original_json, manual_fields_json, upstream_hash, source_hash, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_dataset_state_translation; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_dataset_state_translation (id, record_key, record_source, unique_name, display_name, description, current_json, upstream_json, original_json, manual_fields_json, upstream_hash, source_hash, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_dataset_translation; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_dataset_translation (id, record_key, record_source, source_id, locale, chinese_name, english_name, prime_item, set_item, current_json, upstream_json, original_json, manual_fields_json, upstream_hash, source_hash, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_fish_guide; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_fish_guide (id, record_key, name, source_hash, record_source, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at, current_json, upstream_json, original_json, manual_fields_json, upstream_hash) FROM stdin;
\.


--
-- Data for Name: wf_community_map_feature; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_map_feature (id, record_key, world, feature_type, name, geometry_type, source_hash, record_source, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at, current_json, upstream_json, original_json, manual_fields_json, upstream_hash) FROM stdin;
\.


--
-- Data for Name: wf_community_merge_receipt; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_merge_receipt (preview_id, dataset, committed_at) FROM stdin;
\.


--
-- Data for Name: wf_community_relic; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_relic (id, record_key, relic_name, unique_name, category, era, icon, codex_secret, description_key, quality, reward_manifest, introduced_at, market_id, url_name, vaulted, source_hash, record_source, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at, current_json, upstream_json, original_json, manual_fields_json, upstream_hash) FROM stdin;
\.


--
-- Data for Name: wf_community_relic_drop; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_relic_drop (id, record_key, relic_name, relic_record_key, location_text, rarity, chance, drop_order, source_hash, record_source, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at, current_json, upstream_json, original_json, manual_fields_json, upstream_hash) FROM stdin;
\.


--
-- Data for Name: wf_community_relic_reward; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_relic_reward (id, record_key, relic_record_key, reward_name, unique_name, market_id, url_name, rarity, chance, reward_order, source_hash, record_source, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at, current_json, upstream_json, original_json, manual_fields_json, upstream_hash) FROM stdin;
\.


--
-- Data for Name: wf_community_riven_unrollable; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_riven_unrollable (id, record_key, attribute_code, source_hash, record_source, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at, current_json, upstream_json, original_json, manual_fields_json, upstream_hash) FROM stdin;
\.


--
-- Data for Name: wf_community_term; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_term (id, record_key, term_group, english_text, chinese_text, current_json, upstream_json, original_json, manual_fields_json, record_source, manual_modified, upstream_hash, source_hash, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_worldstate_faction; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_worldstate_faction (id, record_key, locale, display_name, current_json, upstream_json, original_json, manual_fields_json, record_source, manual_modified, upstream_hash, source_hash, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_worldstate_fissure_modifier; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_worldstate_fissure_modifier (id, record_key, locale, display_name, tier_number, current_json, upstream_json, original_json, manual_fields_json, record_source, manual_modified, upstream_hash, source_hash, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_worldstate_language; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_worldstate_language (id, language_key, locale, display_text, record_source, current_json, upstream_json, original_json, manual_fields_json, manual_modified, upstream_hash, source_hash, enabled, created_at, updated_at, display_text_en, display_text_zh, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_worldstate_mission_type; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_worldstate_mission_type (id, record_key, locale, display_name, current_json, upstream_json, original_json, manual_fields_json, record_source, manual_modified, upstream_hash, source_hash, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_worldstate_node; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_worldstate_node (id, record_key, locale, display_name, faction_reference, mission_type_reference, current_json, upstream_json, original_json, manual_fields_json, record_source, manual_modified, upstream_hash, source_hash, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_worldstate_operation_type; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_worldstate_operation_type (id, record_key, locale, display_name, description, current_json, upstream_json, original_json, manual_fields_json, record_source, manual_modified, upstream_hash, source_hash, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_worldstate_persistent_enemy; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_worldstate_persistent_enemy (id, record_key, locale, display_name, enemy_type, current_json, upstream_json, original_json, manual_fields_json, record_source, manual_modified, upstream_hash, source_hash, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_worldstate_rule; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_worldstate_rule (id, record_key, dataset_code, locale, source_hash, record_source, manual_modified, enabled, created_at, updated_at, source_code, last_imported_at, current_json, upstream_json, original_json, manual_fields_json, upstream_hash) FROM stdin;
\.


--
-- Data for Name: wf_community_worldstate_sortie; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_worldstate_sortie (id, record_key, locale, record_type, display_name, description, faction_reference, current_json, upstream_json, original_json, manual_fields_json, record_source, manual_modified, upstream_hash, source_hash, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_worldstate_syndicate; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_worldstate_syndicate (id, record_key, locale, display_name, faction_reference, current_json, upstream_json, original_json, manual_fields_json, record_source, manual_modified, upstream_hash, source_hash, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_community_worldstate_upgrade_type; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_community_worldstate_upgrade_type (id, record_key, locale, display_name, category, current_json, upstream_json, original_json, manual_fields_json, record_source, manual_modified, upstream_hash, source_hash, enabled, created_at, updated_at, source_code, last_imported_at) FROM stdin;
\.


--
-- Data for Name: wf_forum_article; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_forum_article (id, slug, title, summary, tags, category, cover_url, content_html, content_markdown, page_url, enabled, sort_order, created_by_user_id, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: wf_group_watch_rule; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_group_watch_rule (id, group_id, member_id, watch_type, watch_key, watch_value, enabled, notify_enabled, last_match_key, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: wf_manual_item; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_manual_item (id, source_key, official_name, localized_name, normalized_name, wiki_slug, market_slug, remark, raw_json, enabled, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: wf_manual_translation; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_manual_translation (id, item_id, locale, translated_name, remark, enabled, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: wf_official_export_record; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_official_export_record (id, unique_name, export_file, export_type, record_type, official_name, localized_name, parent_name, product_category, exclude_from_codex, description, long_description, wiki_slug, market_slug, raw_json, localized_raw_json, enabled, created_at, updated_at) FROM stdin;
\.


--
-- Data for Name: wf_official_recipe; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_official_recipe (official_record_id, result_type, build_price, build_time, consume_on_use, skip_build_time_price, num, prime_selling_price, always_available, ingredients_json, secret_ingredients_json) FROM stdin;
\.


--
-- Data for Name: wf_official_region; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_official_region (official_record_id, system_name, system_index, node_type, mastery_req, mission_index, faction_index, min_enemy_level, max_enemy_level) FROM stdin;
\.


--
-- Data for Name: wf_official_relic_arcane; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_official_relic_arcane (official_record_id, type, rarity, level_stats_json, relic_rewards_json) FROM stdin;
\.


--
-- Data for Name: wf_official_upgrade; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_official_upgrade (official_record_id, base_drain, fusion_limit, compat_name, type, subtype, polarity, rarity, is_utility, is_augment, is_exilus, num_upgrades_in_set, mod_set, mod_set_values_json, available_challenges_json, level_stats_json, upgrade_entries_json, tags_json) FROM stdin;
\.


--
-- Data for Name: wf_official_warframe; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_official_warframe (official_record_id, health, shield, armor, stamina, power, mastery_req, sprint_speed, codex_secret, abilities_json, exalted_json, stats_json, tags_json) FROM stdin;
\.


--
-- Data for Name: wf_official_weapon; Type: TABLE DATA; Schema: nnmbot; Owner: -
--

COPY nnmbot.wf_official_weapon (official_record_id, mastery_req, total_damage, damage_per_shot_json, critical_chance, critical_multiplier, proc_chance, fire_rate, accuracy, magazine_size, reload_time, multishot, slot, noise, trigger, omega_attenuation, range_json, slam_attack, slide_attack, wind_up, level_stats_json, ingredients_json, tags_json) FROM stdin;
\.


--
-- Name: alias_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.alias_id_seq', 1, false);


--
-- Name: bot_admin_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_admin_id_seq', 1, false);


--
-- Name: bot_channel_instance_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_channel_instance_id_seq', 1, false);


--
-- Name: bot_command_alias_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_command_alias_id_seq', 160, true);


--
-- Name: bot_command_channel_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_command_channel_id_seq', 81, true);


--
-- Name: bot_command_cost_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_command_cost_id_seq', 1, false);


--
-- Name: bot_command_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_command_id_seq', 70, true);


--
-- Name: bot_command_robot_role_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_command_robot_role_id_seq', 104, true);


--
-- Name: bot_command_role_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_command_role_id_seq', 1, false);


--
-- Name: bot_command_template_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_command_template_id_seq', 24, true);


--
-- Name: bot_group_bot_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_group_bot_id_seq', 1, false);


--
-- Name: bot_group_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_group_id_seq', 1, false);


--
-- Name: bot_group_member_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_group_member_id_seq', 1, false);


--
-- Name: bot_group_point_transaction_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_group_point_transaction_id_seq', 1, false);


--
-- Name: bot_robot_role_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_robot_role_id_seq', 1, true);


--
-- Name: bot_robot_role_instance_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_robot_role_instance_id_seq', 1, false);


--
-- Name: bot_scope_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_scope_id_seq', 1, false);


--
-- Name: bot_scope_member_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.bot_scope_member_id_seq', 1, false);


--
-- Name: mission_subscribe_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.mission_subscribe_id_seq', 1, false);


--
-- Name: notification_history_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.notification_history_id_seq', 1, false);


--
-- Name: sys_menu_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.sys_menu_id_seq', 87, true);


--
-- Name: sys_operation_log_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.sys_operation_log_id_seq', 1, false);


--
-- Name: sys_permission_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.sys_permission_id_seq', 54, true);


--
-- Name: sys_role_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.sys_role_id_seq', 3, true);


--
-- Name: sys_user_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.sys_user_id_seq', 1, false);


--
-- Name: sys_user_identity_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.sys_user_identity_id_seq', 1, false);


--
-- Name: wf_community_arbitration_essence_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_arbitration_essence_id_seq', 1, false);


--
-- Name: wf_community_bounty_definition_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_bounty_definition_id_seq', 1, false);


--
-- Name: wf_community_command_alias_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_command_alias_id_seq', 1, false);


--
-- Name: wf_community_dataset_alias_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_dataset_alias_id_seq', 1, false);


--
-- Name: wf_community_dataset_market_component_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_dataset_market_component_id_seq', 13, true);


--
-- Name: wf_community_dataset_market_element_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_dataset_market_element_id_seq', 1, false);


--
-- Name: wf_community_dataset_market_riven_tion_alias_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_dataset_market_riven_tion_alias_id_seq', 23, true);


--
-- Name: wf_community_dataset_market_riven_tion_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_dataset_market_riven_tion_id_seq', 1, false);


--
-- Name: wf_community_dataset_nodes_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_dataset_nodes_id_seq', 1, false);


--
-- Name: wf_community_dataset_reward_pool_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_dataset_reward_pool_id_seq', 1, false);


--
-- Name: wf_community_dataset_riven_analyse_trend_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_dataset_riven_analyse_trend_id_seq', 1, false);


--
-- Name: wf_community_dataset_riven_trend_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_dataset_riven_trend_id_seq', 1, false);


--
-- Name: wf_community_dataset_state_translation_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_dataset_state_translation_id_seq', 1, false);


--
-- Name: wf_community_dataset_translation_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_dataset_translation_id_seq', 1, false);


--
-- Name: wf_community_fish_guide_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_fish_guide_id_seq', 1, false);


--
-- Name: wf_community_map_feature_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_map_feature_id_seq', 1, false);


--
-- Name: wf_community_relic_drop_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_relic_drop_id_seq', 1, false);


--
-- Name: wf_community_relic_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_relic_id_seq', 1, false);


--
-- Name: wf_community_relic_reward_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_relic_reward_id_seq', 1, false);


--
-- Name: wf_community_riven_unrollable_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_riven_unrollable_id_seq', 1, false);


--
-- Name: wf_community_term_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_term_id_seq', 1, false);


--
-- Name: wf_community_worldstate_faction_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_worldstate_faction_id_seq', 1, false);


--
-- Name: wf_community_worldstate_fissure_modifier_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_worldstate_fissure_modifier_id_seq', 1, false);


--
-- Name: wf_community_worldstate_language_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_worldstate_language_id_seq', 1, false);


--
-- Name: wf_community_worldstate_mission_type_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_worldstate_mission_type_id_seq', 1, false);


--
-- Name: wf_community_worldstate_node_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_worldstate_node_id_seq', 1, false);


--
-- Name: wf_community_worldstate_operation_type_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_worldstate_operation_type_id_seq', 1, false);


--
-- Name: wf_community_worldstate_persistent_enemy_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_worldstate_persistent_enemy_id_seq', 1, false);


--
-- Name: wf_community_worldstate_rule_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_worldstate_rule_id_seq', 1, false);


--
-- Name: wf_community_worldstate_sortie_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_worldstate_sortie_id_seq', 1, false);


--
-- Name: wf_community_worldstate_syndicate_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_worldstate_syndicate_id_seq', 1, false);


--
-- Name: wf_community_worldstate_upgrade_type_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_community_worldstate_upgrade_type_id_seq', 1, false);


--
-- Name: wf_group_watch_rule_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_group_watch_rule_id_seq', 1, false);


--
-- Name: wf_manual_item_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_manual_item_id_seq', 1, false);


--
-- Name: wf_manual_translation_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_manual_translation_id_seq', 1, false);


--
-- Name: wf_official_export_record_id_seq; Type: SEQUENCE SET; Schema: nnmbot; Owner: -
--

SELECT pg_catalog.setval('nnmbot.wf_official_export_record_id_seq', 1, false);


--
-- Name: alias alias_alias_name_key; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.alias
    ADD CONSTRAINT alias_alias_name_key UNIQUE (alias_name);


--
-- Name: alias alias_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.alias
    ADD CONSTRAINT alias_pkey PRIMARY KEY (id);


--
-- Name: bot_admin bot_admin_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_admin
    ADD CONSTRAINT bot_admin_pkey PRIMARY KEY (id);


--
-- Name: bot_channel_instance bot_channel_instance_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_channel_instance
    ADD CONSTRAINT bot_channel_instance_pkey PRIMARY KEY (id);


--
-- Name: bot_command_alias bot_command_alias_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_alias
    ADD CONSTRAINT bot_command_alias_pkey PRIMARY KEY (id);


--
-- Name: bot_command_channel bot_command_channel_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_channel
    ADD CONSTRAINT bot_command_channel_pkey PRIMARY KEY (id);


--
-- Name: bot_command bot_command_code_key; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command
    ADD CONSTRAINT bot_command_code_key UNIQUE (code);


--
-- Name: bot_command_cost bot_command_cost_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_cost
    ADD CONSTRAINT bot_command_cost_pkey PRIMARY KEY (id);


--
-- Name: bot_command bot_command_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command
    ADD CONSTRAINT bot_command_pkey PRIMARY KEY (id);


--
-- Name: bot_command_robot_role bot_command_robot_role_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_robot_role
    ADD CONSTRAINT bot_command_robot_role_pkey PRIMARY KEY (id);


--
-- Name: bot_command_role bot_command_role_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_role
    ADD CONSTRAINT bot_command_role_pkey PRIMARY KEY (id);


--
-- Name: bot_command_template bot_command_template_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_template
    ADD CONSTRAINT bot_command_template_pkey PRIMARY KEY (id);


--
-- Name: bot_command_template bot_command_template_template_code_key; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_template
    ADD CONSTRAINT bot_command_template_template_code_key UNIQUE (template_code);


--
-- Name: bot_group_bot bot_group_bot_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_group_bot
    ADD CONSTRAINT bot_group_bot_pkey PRIMARY KEY (id);


--
-- Name: bot_group_member bot_group_member_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_group_member
    ADD CONSTRAINT bot_group_member_pkey PRIMARY KEY (id);


--
-- Name: bot_group bot_group_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_group
    ADD CONSTRAINT bot_group_pkey PRIMARY KEY (id);


--
-- Name: bot_group_point_transaction bot_group_point_transaction_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_group_point_transaction
    ADD CONSTRAINT bot_group_point_transaction_pkey PRIMARY KEY (id);


--
-- Name: bot_onebot_config bot_onebot_config_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_onebot_config
    ADD CONSTRAINT bot_onebot_config_pkey PRIMARY KEY (instance_id);


--
-- Name: bot_qq_official_config bot_qq_official_config_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_qq_official_config
    ADD CONSTRAINT bot_qq_official_config_pkey PRIMARY KEY (instance_id);


--
-- Name: bot_robot_role_instance bot_robot_role_instance_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_robot_role_instance
    ADD CONSTRAINT bot_robot_role_instance_pkey PRIMARY KEY (id);


--
-- Name: bot_robot_role bot_robot_role_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_robot_role
    ADD CONSTRAINT bot_robot_role_pkey PRIMARY KEY (id);


--
-- Name: bot_scope_member bot_scope_member_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_scope_member
    ADD CONSTRAINT bot_scope_member_pkey PRIMARY KEY (id);


--
-- Name: bot_scope bot_scope_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_scope
    ADD CONSTRAINT bot_scope_pkey PRIMARY KEY (id);


--
-- Name: mission_subscribe mission_subscribe_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.mission_subscribe
    ADD CONSTRAINT mission_subscribe_pkey PRIMARY KEY (id);


--
-- Name: notification_history notification_history_event_type_event_id_group_id_key; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.notification_history
    ADD CONSTRAINT notification_history_event_type_event_id_group_id_key UNIQUE (event_type, event_id, group_id);


--
-- Name: notification_history notification_history_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.notification_history
    ADD CONSTRAINT notification_history_pkey PRIMARY KEY (id);


--
-- Name: sys_menu sys_menu_name_key; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_menu
    ADD CONSTRAINT sys_menu_name_key UNIQUE (name);


--
-- Name: sys_menu sys_menu_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_menu
    ADD CONSTRAINT sys_menu_pkey PRIMARY KEY (id);


--
-- Name: sys_operation_log sys_operation_log_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_operation_log
    ADD CONSTRAINT sys_operation_log_pkey PRIMARY KEY (id);


--
-- Name: sys_permission sys_permission_code_key; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_permission
    ADD CONSTRAINT sys_permission_code_key UNIQUE (code);


--
-- Name: sys_permission sys_permission_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_permission
    ADD CONSTRAINT sys_permission_pkey PRIMARY KEY (id);


--
-- Name: sys_role sys_role_code_key; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_role
    ADD CONSTRAINT sys_role_code_key UNIQUE (code);


--
-- Name: sys_role_menu sys_role_menu_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_role_menu
    ADD CONSTRAINT sys_role_menu_pkey PRIMARY KEY (role_id, menu_id);


--
-- Name: sys_role_permission sys_role_permission_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_role_permission
    ADD CONSTRAINT sys_role_permission_pkey PRIMARY KEY (role_id, permission_id);


--
-- Name: sys_role sys_role_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_role
    ADD CONSTRAINT sys_role_pkey PRIMARY KEY (id);


--
-- Name: sys_user_identity sys_user_identity_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_user_identity
    ADD CONSTRAINT sys_user_identity_pkey PRIMARY KEY (id);


--
-- Name: sys_user sys_user_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_user
    ADD CONSTRAINT sys_user_pkey PRIMARY KEY (id);


--
-- Name: sys_user_role sys_user_role_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_user_role
    ADD CONSTRAINT sys_user_role_pkey PRIMARY KEY (user_id, role_id);


--
-- Name: sys_user sys_user_username_key; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_user
    ADD CONSTRAINT sys_user_username_key UNIQUE (username);


--
-- Name: bot_channel_instance uq_bot_channel_instance_account; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_channel_instance
    ADD CONSTRAINT uq_bot_channel_instance_account UNIQUE (platform, bot_account_id);


--
-- Name: bot_channel_instance uq_bot_channel_instance_id; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_channel_instance
    ADD CONSTRAINT uq_bot_channel_instance_id UNIQUE (instance_id);


--
-- Name: bot_command_alias uq_bot_command_alias; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_alias
    ADD CONSTRAINT uq_bot_command_alias UNIQUE (command_id, alias_name);


--
-- Name: bot_command_channel uq_bot_command_channel; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_channel
    ADD CONSTRAINT uq_bot_command_channel UNIQUE (command_id, platform);


--
-- Name: bot_command_cost uq_bot_command_cost_command; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_cost
    ADD CONSTRAINT uq_bot_command_cost_command UNIQUE (command_id);


--
-- Name: bot_command_robot_role uq_bot_command_robot_role; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_robot_role
    ADD CONSTRAINT uq_bot_command_robot_role UNIQUE (command_id, robot_role_id);


--
-- Name: bot_command_role uq_bot_command_role; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_command_role
    ADD CONSTRAINT uq_bot_command_role UNIQUE (command_id, role_id);


--
-- Name: bot_group_bot uq_bot_group_bot; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_group_bot
    ADD CONSTRAINT uq_bot_group_bot UNIQUE (group_id, bot_instance_id, platform, scope_type, scope_key);


--
-- Name: bot_group uq_bot_group_code; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_group
    ADD CONSTRAINT uq_bot_group_code UNIQUE (code);


--
-- Name: bot_group_point_transaction uq_bot_group_point_transaction_key; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_group_point_transaction
    ADD CONSTRAINT uq_bot_group_point_transaction_key UNIQUE (idempotency_key);


--
-- Name: bot_qq_official_config uq_bot_qq_official_app_id; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_qq_official_config
    ADD CONSTRAINT uq_bot_qq_official_app_id UNIQUE (app_id);


--
-- Name: bot_robot_role uq_bot_robot_role_code; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_robot_role
    ADD CONSTRAINT uq_bot_robot_role_code UNIQUE (code);


--
-- Name: bot_robot_role_instance uq_bot_robot_role_instance; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_robot_role_instance
    ADD CONSTRAINT uq_bot_robot_role_instance UNIQUE (robot_role_id, bot_instance_id);


--
-- Name: bot_scope uq_bot_scope; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_scope
    ADD CONSTRAINT uq_bot_scope UNIQUE (platform, scope_type, scope_key, bot_instance_id);


--
-- Name: bot_scope_member uq_bot_scope_member; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.bot_scope_member
    ADD CONSTRAINT uq_bot_scope_member UNIQUE (scope_id, platform_user_id);


--
-- Name: sys_user_identity uq_sys_user_identity; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.sys_user_identity
    ADD CONSTRAINT uq_sys_user_identity UNIQUE (platform, external_user_id);


--
-- Name: wf_community_bounty_definition uq_wf_bounty_source_key; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_bounty_definition
    ADD CONSTRAINT uq_wf_bounty_source_key UNIQUE (source_code, record_key);


--
-- Name: wf_community_dataset_market_component uq_wf_community_dataset_market_component_alias; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_market_component
    ADD CONSTRAINT uq_wf_community_dataset_market_component_alias UNIQUE (input_alias);


--
-- Name: wf_group_watch_rule uq_wf_group_watch_rule; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_group_watch_rule
    ADD CONSTRAINT uq_wf_group_watch_rule UNIQUE (group_id, member_id, watch_type, watch_key, watch_value);


--
-- Name: wf_manual_translation uq_wf_manual_translation_item_locale; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_manual_translation
    ADD CONSTRAINT uq_wf_manual_translation_item_locale UNIQUE (item_id, locale);


--
-- Name: wf_official_export_record uq_wf_official_export_identity; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_official_export_record
    ADD CONSTRAINT uq_wf_official_export_identity UNIQUE (export_type, unique_name);


--
-- Name: wf_arbitration_schedule wf_arbitration_schedule_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_arbitration_schedule
    ADD CONSTRAINT wf_arbitration_schedule_pkey PRIMARY KEY (activation);


--
-- Name: wf_community_arbitration_essence wf_community_arbitration_essence_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_arbitration_essence
    ADD CONSTRAINT wf_community_arbitration_essence_pkey PRIMARY KEY (id);


--
-- Name: wf_community_bounty_definition wf_community_bounty_definition_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_bounty_definition
    ADD CONSTRAINT wf_community_bounty_definition_pkey PRIMARY KEY (id);


--
-- Name: wf_community_bounty_stage wf_community_bounty_stage_definition_id_stage_number_option_key; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_bounty_stage
    ADD CONSTRAINT wf_community_bounty_stage_definition_id_stage_number_option_key UNIQUE (definition_id, stage_number, option_number);


--
-- Name: wf_community_bounty_stage wf_community_bounty_stage_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_bounty_stage
    ADD CONSTRAINT wf_community_bounty_stage_pkey PRIMARY KEY (definition_id, entry_order);


--
-- Name: wf_community_command_alias wf_community_command_alias_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_command_alias
    ADD CONSTRAINT wf_community_command_alias_pkey PRIMARY KEY (id);


--
-- Name: wf_community_dataset_alias wf_community_dataset_alias_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_alias
    ADD CONSTRAINT wf_community_dataset_alias_pkey PRIMARY KEY (id);


--
-- Name: wf_community_dataset_market_component wf_community_dataset_market_component_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_market_component
    ADD CONSTRAINT wf_community_dataset_market_component_pkey PRIMARY KEY (id);


--
-- Name: wf_community_dataset_market_element wf_community_dataset_market_element_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_market_element
    ADD CONSTRAINT wf_community_dataset_market_element_pkey PRIMARY KEY (id);


--
-- Name: wf_community_dataset_market_riven_tion_alias wf_community_dataset_market_riven_tion_alias_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_market_riven_tion_alias
    ADD CONSTRAINT wf_community_dataset_market_riven_tion_alias_pkey PRIMARY KEY (id);


--
-- Name: wf_community_dataset_market_riven_tion wf_community_dataset_market_riven_tion_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_market_riven_tion
    ADD CONSTRAINT wf_community_dataset_market_riven_tion_pkey PRIMARY KEY (id);


--
-- Name: wf_community_dataset_nodes wf_community_dataset_nodes_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_nodes
    ADD CONSTRAINT wf_community_dataset_nodes_pkey PRIMARY KEY (id);


--
-- Name: wf_community_dataset_reward_pool wf_community_dataset_reward_pool_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_reward_pool
    ADD CONSTRAINT wf_community_dataset_reward_pool_pkey PRIMARY KEY (id);


--
-- Name: wf_community_dataset_riven_analyse_trend wf_community_dataset_riven_analyse_trend_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_riven_analyse_trend
    ADD CONSTRAINT wf_community_dataset_riven_analyse_trend_pkey PRIMARY KEY (id);


--
-- Name: wf_community_dataset_riven_trend wf_community_dataset_riven_trend_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_riven_trend
    ADD CONSTRAINT wf_community_dataset_riven_trend_pkey PRIMARY KEY (id);


--
-- Name: wf_community_dataset_state_translation wf_community_dataset_state_translation_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_state_translation
    ADD CONSTRAINT wf_community_dataset_state_translation_pkey PRIMARY KEY (id);


--
-- Name: wf_community_dataset_translation wf_community_dataset_translation_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_dataset_translation
    ADD CONSTRAINT wf_community_dataset_translation_pkey PRIMARY KEY (id);


--
-- Name: wf_community_fish_guide wf_community_fish_guide_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_fish_guide
    ADD CONSTRAINT wf_community_fish_guide_pkey PRIMARY KEY (id);


--
-- Name: wf_community_map_feature wf_community_map_feature_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_map_feature
    ADD CONSTRAINT wf_community_map_feature_pkey PRIMARY KEY (id);


--
-- Name: wf_community_merge_receipt wf_community_merge_receipt_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_merge_receipt
    ADD CONSTRAINT wf_community_merge_receipt_pkey PRIMARY KEY (preview_id);


--
-- Name: wf_community_relic_drop wf_community_relic_drop_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_relic_drop
    ADD CONSTRAINT wf_community_relic_drop_pkey PRIMARY KEY (id);


--
-- Name: wf_community_relic wf_community_relic_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_relic
    ADD CONSTRAINT wf_community_relic_pkey PRIMARY KEY (id);


--
-- Name: wf_community_relic_reward wf_community_relic_reward_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_relic_reward
    ADD CONSTRAINT wf_community_relic_reward_pkey PRIMARY KEY (id);


--
-- Name: wf_community_riven_unrollable wf_community_riven_unrollable_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_riven_unrollable
    ADD CONSTRAINT wf_community_riven_unrollable_pkey PRIMARY KEY (id);


--
-- Name: wf_community_term wf_community_term_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_term
    ADD CONSTRAINT wf_community_term_pkey PRIMARY KEY (id);


--
-- Name: wf_community_worldstate_faction wf_community_worldstate_faction_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_faction
    ADD CONSTRAINT wf_community_worldstate_faction_pkey PRIMARY KEY (id);


--
-- Name: wf_community_worldstate_fissure_modifier wf_community_worldstate_fissure_modifier_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_fissure_modifier
    ADD CONSTRAINT wf_community_worldstate_fissure_modifier_pkey PRIMARY KEY (id);


--
-- Name: wf_community_worldstate_language wf_community_worldstate_language_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_language
    ADD CONSTRAINT wf_community_worldstate_language_pkey PRIMARY KEY (id);


--
-- Name: wf_community_worldstate_mission_type wf_community_worldstate_mission_type_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_mission_type
    ADD CONSTRAINT wf_community_worldstate_mission_type_pkey PRIMARY KEY (id);


--
-- Name: wf_community_worldstate_node wf_community_worldstate_node_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_node
    ADD CONSTRAINT wf_community_worldstate_node_pkey PRIMARY KEY (id);


--
-- Name: wf_community_worldstate_operation_type wf_community_worldstate_operation_type_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_operation_type
    ADD CONSTRAINT wf_community_worldstate_operation_type_pkey PRIMARY KEY (id);


--
-- Name: wf_community_worldstate_persistent_enemy wf_community_worldstate_persistent_enemy_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_persistent_enemy
    ADD CONSTRAINT wf_community_worldstate_persistent_enemy_pkey PRIMARY KEY (id);


--
-- Name: wf_community_worldstate_rule wf_community_worldstate_rule_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_rule
    ADD CONSTRAINT wf_community_worldstate_rule_pkey PRIMARY KEY (id);


--
-- Name: wf_community_worldstate_sortie wf_community_worldstate_sortie_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_sortie
    ADD CONSTRAINT wf_community_worldstate_sortie_pkey PRIMARY KEY (id);


--
-- Name: wf_community_worldstate_syndicate wf_community_worldstate_syndicate_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_syndicate
    ADD CONSTRAINT wf_community_worldstate_syndicate_pkey PRIMARY KEY (id);


--
-- Name: wf_community_worldstate_upgrade_type wf_community_worldstate_upgrade_type_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_worldstate_upgrade_type
    ADD CONSTRAINT wf_community_worldstate_upgrade_type_pkey PRIMARY KEY (id);


--
-- Name: wf_forum_article wf_forum_article_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_forum_article
    ADD CONSTRAINT wf_forum_article_pkey PRIMARY KEY (id);


--
-- Name: wf_forum_article wf_forum_article_slug_key; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_forum_article
    ADD CONSTRAINT wf_forum_article_slug_key UNIQUE (slug);


--
-- Name: wf_group_watch_rule wf_group_watch_rule_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_group_watch_rule
    ADD CONSTRAINT wf_group_watch_rule_pkey PRIMARY KEY (id);


--
-- Name: wf_manual_item wf_manual_item_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_manual_item
    ADD CONSTRAINT wf_manual_item_pkey PRIMARY KEY (id);


--
-- Name: wf_manual_item wf_manual_item_source_key_key; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_manual_item
    ADD CONSTRAINT wf_manual_item_source_key_key UNIQUE (source_key);


--
-- Name: wf_manual_translation wf_manual_translation_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_manual_translation
    ADD CONSTRAINT wf_manual_translation_pkey PRIMARY KEY (id);


--
-- Name: wf_official_export_record wf_official_export_record_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_official_export_record
    ADD CONSTRAINT wf_official_export_record_pkey PRIMARY KEY (id);


--
-- Name: wf_official_recipe wf_official_recipe_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_official_recipe
    ADD CONSTRAINT wf_official_recipe_pkey PRIMARY KEY (official_record_id);


--
-- Name: wf_official_region wf_official_region_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_official_region
    ADD CONSTRAINT wf_official_region_pkey PRIMARY KEY (official_record_id);


--
-- Name: wf_official_relic_arcane wf_official_relic_arcane_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_official_relic_arcane
    ADD CONSTRAINT wf_official_relic_arcane_pkey PRIMARY KEY (official_record_id);


--
-- Name: wf_official_upgrade wf_official_upgrade_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_official_upgrade
    ADD CONSTRAINT wf_official_upgrade_pkey PRIMARY KEY (official_record_id);


--
-- Name: wf_official_warframe wf_official_warframe_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_official_warframe
    ADD CONSTRAINT wf_official_warframe_pkey PRIMARY KEY (official_record_id);


--
-- Name: wf_official_weapon wf_official_weapon_pkey; Type: CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_official_weapon
    ADD CONSTRAINT wf_official_weapon_pkey PRIMARY KEY (official_record_id);


--
-- Name: idx_bot_command_cost_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_bot_command_cost_enabled ON nnmbot.bot_command_cost USING btree (enabled, reward_enabled, command_id);


--
-- Name: idx_bot_command_game; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_bot_command_game ON nnmbot.bot_command USING btree (game_code);


--
-- Name: idx_bot_command_robot_role_command; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_bot_command_robot_role_command ON nnmbot.bot_command_robot_role USING btree (command_id);


--
-- Name: idx_bot_command_template_command_order; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_bot_command_template_command_order ON nnmbot.bot_command_template USING btree (command_id, sort_order, id);


--
-- Name: idx_bot_group_bot_group; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_bot_group_bot_group ON nnmbot.bot_group_bot USING btree (group_id);


--
-- Name: idx_bot_group_bot_instance; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_bot_group_bot_instance ON nnmbot.bot_group_bot USING btree (bot_instance_id);


--
-- Name: idx_bot_group_member_group; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_bot_group_member_group ON nnmbot.bot_group_member USING btree (group_id);


--
-- Name: idx_bot_group_point_transaction_group; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_bot_group_point_transaction_group ON nnmbot.bot_group_point_transaction USING btree (group_id, created_at DESC);


--
-- Name: idx_bot_group_point_transaction_member; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_bot_group_point_transaction_member ON nnmbot.bot_group_point_transaction USING btree (member_id, created_at DESC);


--
-- Name: idx_bot_robot_role_instance_bot; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_bot_robot_role_instance_bot ON nnmbot.bot_robot_role_instance USING btree (bot_instance_id);


--
-- Name: idx_bot_robot_role_instance_role; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_bot_robot_role_instance_role ON nnmbot.bot_robot_role_instance USING btree (robot_role_id);


--
-- Name: idx_bot_scope_instance; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_bot_scope_instance ON nnmbot.bot_scope USING btree (bot_instance_id);


--
-- Name: idx_bot_scope_member_user; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_bot_scope_member_user ON nnmbot.bot_scope_member USING btree (user_id);


--
-- Name: idx_sys_menu_parent_id; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_sys_menu_parent_id ON nnmbot.sys_menu USING btree (parent_id);


--
-- Name: idx_sys_operation_log_created_at; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_sys_operation_log_created_at ON nnmbot.sys_operation_log USING btree (created_at);


--
-- Name: idx_sys_operation_log_operator_id; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_sys_operation_log_operator_id ON nnmbot.sys_operation_log USING btree (operator_id);


--
-- Name: idx_sys_permission_menu_id; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_sys_permission_menu_id ON nnmbot.sys_permission USING btree (menu_id);


--
-- Name: idx_wf_arbitration_expiry; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_arbitration_expiry ON nnmbot.wf_arbitration_schedule USING btree (expiry);


--
-- Name: idx_wf_arbitration_fetched; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_arbitration_fetched ON nnmbot.wf_arbitration_schedule USING btree (fetched_at DESC);


--
-- Name: idx_wf_community_dataset_alias_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_community_dataset_alias_enabled ON nnmbot.wf_community_dataset_alias USING btree (enabled, record_key);


--
-- Name: idx_wf_community_dataset_alias_normalized; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_community_dataset_alias_normalized ON nnmbot.wf_community_dataset_alias USING btree (enabled, normalized_alias);


--
-- Name: idx_wf_community_dataset_market_component_enabled_alias; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_community_dataset_market_component_enabled_alias ON nnmbot.wf_community_dataset_market_component USING btree (enabled, input_alias);


--
-- Name: idx_wf_community_dataset_market_element_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_community_dataset_market_element_enabled ON nnmbot.wf_community_dataset_market_element USING btree (enabled, record_key);


--
-- Name: idx_wf_community_dataset_market_riven_tion_alias_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_community_dataset_market_riven_tion_alias_enabled ON nnmbot.wf_community_dataset_market_riven_tion_alias USING btree (enabled, record_key);


--
-- Name: idx_wf_community_dataset_market_riven_tion_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_community_dataset_market_riven_tion_enabled ON nnmbot.wf_community_dataset_market_riven_tion USING btree (enabled, record_key);


--
-- Name: idx_wf_community_dataset_nodes_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_community_dataset_nodes_enabled ON nnmbot.wf_community_dataset_nodes USING btree (enabled, record_key);


--
-- Name: idx_wf_community_dataset_reward_pool_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_community_dataset_reward_pool_enabled ON nnmbot.wf_community_dataset_reward_pool USING btree (enabled, record_key);


--
-- Name: idx_wf_community_dataset_riven_analyse_trend_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_community_dataset_riven_analyse_trend_enabled ON nnmbot.wf_community_dataset_riven_analyse_trend USING btree (enabled, record_key);


--
-- Name: idx_wf_community_dataset_riven_trend_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_community_dataset_riven_trend_enabled ON nnmbot.wf_community_dataset_riven_trend USING btree (enabled, record_key);


--
-- Name: idx_wf_community_dataset_state_translation_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_community_dataset_state_translation_enabled ON nnmbot.wf_community_dataset_state_translation USING btree (enabled, record_key);


--
-- Name: idx_wf_community_dataset_translation_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_community_dataset_translation_enabled ON nnmbot.wf_community_dataset_translation USING btree (enabled, record_key);


--
-- Name: idx_wf_community_relic_reward_relic; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_community_relic_reward_relic ON nnmbot.wf_community_relic_reward USING btree (relic_record_key, reward_order);


--
-- Name: idx_wf_community_worldstate_language_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_community_worldstate_language_enabled ON nnmbot.wf_community_worldstate_language USING btree (enabled, language_key, locale);


--
-- Name: idx_wf_community_worldstate_rule_dataset_locale; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_community_worldstate_rule_dataset_locale ON nnmbot.wf_community_worldstate_rule USING btree (dataset_code, locale);


--
-- Name: idx_wf_forum_article_category; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_forum_article_category ON nnmbot.wf_forum_article USING btree (enabled, category, sort_order, id);


--
-- Name: idx_wf_forum_article_enabled_order; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_forum_article_enabled_order ON nnmbot.wf_forum_article USING btree (enabled, sort_order, id);


--
-- Name: idx_wf_group_watch_rule_group; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_group_watch_rule_group ON nnmbot.wf_group_watch_rule USING btree (group_id, enabled, updated_at DESC);


--
-- Name: idx_wf_group_watch_rule_member; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_group_watch_rule_member ON nnmbot.wf_group_watch_rule USING btree (member_id, enabled);


--
-- Name: idx_wf_manual_item_enabled_name; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_manual_item_enabled_name ON nnmbot.wf_manual_item USING btree (enabled, localized_name);


--
-- Name: idx_wf_manual_translation_enabled_item; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_manual_translation_enabled_item ON nnmbot.wf_manual_translation USING btree (enabled, item_id, locale);


--
-- Name: idx_wf_official_enabled_name; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_official_enabled_name ON nnmbot.wf_official_export_record USING btree (enabled, localized_name);


--
-- Name: idx_wf_official_market_slug; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_official_market_slug ON nnmbot.wf_official_export_record USING btree (market_slug);


--
-- Name: idx_wf_ws_faction_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_ws_faction_enabled ON nnmbot.wf_community_worldstate_faction USING btree (enabled, record_key, locale);


--
-- Name: idx_wf_ws_fissure_modifier_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_ws_fissure_modifier_enabled ON nnmbot.wf_community_worldstate_fissure_modifier USING btree (enabled, tier_number, record_key);


--
-- Name: idx_wf_ws_mission_type_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_ws_mission_type_enabled ON nnmbot.wf_community_worldstate_mission_type USING btree (enabled, record_key, locale);


--
-- Name: idx_wf_ws_node_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_ws_node_enabled ON nnmbot.wf_community_worldstate_node USING btree (enabled, record_key, locale);


--
-- Name: idx_wf_ws_operation_type_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_ws_operation_type_enabled ON nnmbot.wf_community_worldstate_operation_type USING btree (enabled, record_key, locale);


--
-- Name: idx_wf_ws_persistent_enemy_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_ws_persistent_enemy_enabled ON nnmbot.wf_community_worldstate_persistent_enemy USING btree (enabled, record_key, locale);


--
-- Name: idx_wf_ws_sortie_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_ws_sortie_enabled ON nnmbot.wf_community_worldstate_sortie USING btree (enabled, record_type, record_key, locale);


--
-- Name: idx_wf_ws_syndicate_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_ws_syndicate_enabled ON nnmbot.wf_community_worldstate_syndicate USING btree (enabled, record_key, locale);


--
-- Name: idx_wf_ws_upgrade_type_enabled; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE INDEX idx_wf_ws_upgrade_type_enabled ON nnmbot.wf_community_worldstate_upgrade_type USING btree (enabled, record_key, locale);


--
-- Name: uq_bot_group_member_kook; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_bot_group_member_kook ON nnmbot.bot_group_member USING btree (group_id, kook_id) WHERE (kook_id IS NOT NULL);


--
-- Name: uq_bot_group_member_openid; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_bot_group_member_openid ON nnmbot.bot_group_member USING btree (group_id, member_openid) WHERE (member_openid IS NOT NULL);


--
-- Name: uq_bot_group_member_qq; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_bot_group_member_qq ON nnmbot.bot_group_member USING btree (group_id, qq_id) WHERE (qq_id IS NOT NULL);


--
-- Name: uq_wf_community_arbitration_essence_src_record_5ecf5f6d; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_arbitration_essence_src_record_5ecf5f6d ON nnmbot.wf_community_arbitration_essence USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_arbitration_essence_src_record_5ecf5f6d; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_arbitration_essence_src_record_5ecf5f6d IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_command_alias_src_record_ada91aef; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_command_alias_src_record_ada91aef ON nnmbot.wf_community_command_alias USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_command_alias_src_record_ada91aef; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_command_alias_src_record_ada91aef IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_dataset_alias_manual_key; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_dataset_alias_manual_key ON nnmbot.wf_community_dataset_alias USING btree (manual_item_id, normalized_alias) WHERE (manual_item_id IS NOT NULL);


--
-- Name: uq_wf_community_dataset_alias_src_record_26941fe8; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_dataset_alias_src_record_26941fe8 ON nnmbot.wf_community_dataset_alias USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_dataset_alias_src_record_26941fe8; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_dataset_alias_src_record_26941fe8 IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_dataset_market_compone_src_record_fc4fd2c6; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_dataset_market_compone_src_record_fc4fd2c6 ON nnmbot.wf_community_dataset_market_component USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_dataset_market_compone_src_record_fc4fd2c6; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_dataset_market_compone_src_record_fc4fd2c6 IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_dataset_market_element_src_record_d81a988b; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_dataset_market_element_src_record_d81a988b ON nnmbot.wf_community_dataset_market_element USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_dataset_market_element_src_record_d81a988b; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_dataset_market_element_src_record_d81a988b IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_dataset_market_riven_t_src_record_6706ab7e; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_dataset_market_riven_t_src_record_6706ab7e ON nnmbot.wf_community_dataset_market_riven_tion_alias USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_dataset_market_riven_t_src_record_6706ab7e; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_dataset_market_riven_t_src_record_6706ab7e IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_dataset_market_riven_t_src_record_82ee2908; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_dataset_market_riven_t_src_record_82ee2908 ON nnmbot.wf_community_dataset_market_riven_tion USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_dataset_market_riven_t_src_record_82ee2908; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_dataset_market_riven_t_src_record_82ee2908 IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_dataset_nodes_src_record_47804c0d; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_dataset_nodes_src_record_47804c0d ON nnmbot.wf_community_dataset_nodes USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_dataset_nodes_src_record_47804c0d; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_dataset_nodes_src_record_47804c0d IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_dataset_reward_pool_src_record_4db368e8; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_dataset_reward_pool_src_record_4db368e8 ON nnmbot.wf_community_dataset_reward_pool USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_dataset_reward_pool_src_record_4db368e8; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_dataset_reward_pool_src_record_4db368e8 IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_dataset_riven_analyse__src_record_e5b7349f; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_dataset_riven_analyse__src_record_e5b7349f ON nnmbot.wf_community_dataset_riven_analyse_trend USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_dataset_riven_analyse__src_record_e5b7349f; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_dataset_riven_analyse__src_record_e5b7349f IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_dataset_riven_trend_src_record_630341bc; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_dataset_riven_trend_src_record_630341bc ON nnmbot.wf_community_dataset_riven_trend USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_dataset_riven_trend_src_record_630341bc; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_dataset_riven_trend_src_record_630341bc IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_dataset_state_translat_src_record_c42bdbc7; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_dataset_state_translat_src_record_c42bdbc7 ON nnmbot.wf_community_dataset_state_translation USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_dataset_state_translat_src_record_c42bdbc7; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_dataset_state_translat_src_record_c42bdbc7 IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_dataset_translation_src_record_4de03b93; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_dataset_translation_src_record_4de03b93 ON nnmbot.wf_community_dataset_translation USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_dataset_translation_src_record_4de03b93; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_dataset_translation_src_record_4de03b93 IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_fish_guide_src_record_5ce9c622; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_fish_guide_src_record_5ce9c622 ON nnmbot.wf_community_fish_guide USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_fish_guide_src_record_5ce9c622; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_fish_guide_src_record_5ce9c622 IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_map_feature_src_record_0d6a8950; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_map_feature_src_record_0d6a8950 ON nnmbot.wf_community_map_feature USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_map_feature_src_record_0d6a8950; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_map_feature_src_record_0d6a8950 IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_relic_drop_src_record_59d62e97; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_relic_drop_src_record_59d62e97 ON nnmbot.wf_community_relic_drop USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_relic_drop_src_record_59d62e97; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_relic_drop_src_record_59d62e97 IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_relic_reward_src_record_9905fe8d; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_relic_reward_src_record_9905fe8d ON nnmbot.wf_community_relic_reward USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_relic_reward_src_record_9905fe8d; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_relic_reward_src_record_9905fe8d IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_relic_src_record_f8a9a74d; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_relic_src_record_f8a9a74d ON nnmbot.wf_community_relic USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_relic_src_record_f8a9a74d; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_relic_src_record_f8a9a74d IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_riven_unrollable_src_record_85024979; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_riven_unrollable_src_record_85024979 ON nnmbot.wf_community_riven_unrollable USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_riven_unrollable_src_record_85024979; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_riven_unrollable_src_record_85024979 IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_term_src_record_7c7be9af; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_term_src_record_7c7be9af ON nnmbot.wf_community_term USING btree (source_code, record_key);


--
-- Name: INDEX uq_wf_community_term_src_record_7c7be9af; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_term_src_record_7c7be9af IS '同一来源内按 record_key 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_worldstate_faction_src_record_042bf926; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_worldstate_faction_src_record_042bf926 ON nnmbot.wf_community_worldstate_faction USING btree (source_code, record_key, locale);


--
-- Name: INDEX uq_wf_community_worldstate_faction_src_record_042bf926; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_worldstate_faction_src_record_042bf926 IS '同一来源内按 record_key 和 locale 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_worldstate_fissure_mod_src_record_cccb70df; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_worldstate_fissure_mod_src_record_cccb70df ON nnmbot.wf_community_worldstate_fissure_modifier USING btree (source_code, record_key, locale);


--
-- Name: INDEX uq_wf_community_worldstate_fissure_mod_src_record_cccb70df; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_worldstate_fissure_mod_src_record_cccb70df IS '同一来源内按 record_key 和 locale 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_worldstate_language_src_record_9d11e61c; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_worldstate_language_src_record_9d11e61c ON nnmbot.wf_community_worldstate_language USING btree (source_code, language_key, locale);


--
-- Name: INDEX uq_wf_community_worldstate_language_src_record_9d11e61c; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_worldstate_language_src_record_9d11e61c IS '同一来源内按 language_key 和 locale 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_worldstate_mission_typ_src_record_850763bb; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_worldstate_mission_typ_src_record_850763bb ON nnmbot.wf_community_worldstate_mission_type USING btree (source_code, record_key, locale);


--
-- Name: INDEX uq_wf_community_worldstate_mission_typ_src_record_850763bb; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_worldstate_mission_typ_src_record_850763bb IS '同一来源内按 record_key 和 locale 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_worldstate_node_src_record_3632ad93; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_worldstate_node_src_record_3632ad93 ON nnmbot.wf_community_worldstate_node USING btree (source_code, record_key, locale);


--
-- Name: INDEX uq_wf_community_worldstate_node_src_record_3632ad93; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_worldstate_node_src_record_3632ad93 IS '同一来源内按 record_key 和 locale 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_worldstate_operation_t_src_record_05d36e9b; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_worldstate_operation_t_src_record_05d36e9b ON nnmbot.wf_community_worldstate_operation_type USING btree (source_code, record_key, locale);


--
-- Name: INDEX uq_wf_community_worldstate_operation_t_src_record_05d36e9b; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_worldstate_operation_t_src_record_05d36e9b IS '同一来源内按 record_key 和 locale 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_worldstate_persistent__src_record_1efa55e4; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_worldstate_persistent__src_record_1efa55e4 ON nnmbot.wf_community_worldstate_persistent_enemy USING btree (source_code, record_key, locale);


--
-- Name: INDEX uq_wf_community_worldstate_persistent__src_record_1efa55e4; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_worldstate_persistent__src_record_1efa55e4 IS '同一来源内按 record_key 和 locale 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_worldstate_rule_src_record_c3a52385; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_worldstate_rule_src_record_c3a52385 ON nnmbot.wf_community_worldstate_rule USING btree (source_code, record_key, locale);


--
-- Name: INDEX uq_wf_community_worldstate_rule_src_record_c3a52385; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_worldstate_rule_src_record_c3a52385 IS '同一来源内按 record_key 和 locale 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_worldstate_sortie_src_record_a520addd; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_worldstate_sortie_src_record_a520addd ON nnmbot.wf_community_worldstate_sortie USING btree (source_code, record_key, locale, record_type);


--
-- Name: INDEX uq_wf_community_worldstate_sortie_src_record_a520addd; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_worldstate_sortie_src_record_a520addd IS '同一来源内按 record_key、locale 和 record_type 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_worldstate_syndicate_src_record_7e3c7148; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_worldstate_syndicate_src_record_7e3c7148 ON nnmbot.wf_community_worldstate_syndicate USING btree (source_code, record_key, locale);


--
-- Name: INDEX uq_wf_community_worldstate_syndicate_src_record_7e3c7148; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_worldstate_syndicate_src_record_7e3c7148 IS '同一来源内按 record_key 和 locale 唯一，不同来源允许同键共存';


--
-- Name: uq_wf_community_worldstate_upgrade_typ_src_record_48ccec3c; Type: INDEX; Schema: nnmbot; Owner: -
--

CREATE UNIQUE INDEX uq_wf_community_worldstate_upgrade_typ_src_record_48ccec3c ON nnmbot.wf_community_worldstate_upgrade_type USING btree (source_code, record_key, locale);


--
-- Name: INDEX uq_wf_community_worldstate_upgrade_typ_src_record_48ccec3c; Type: COMMENT; Schema: nnmbot; Owner: -
--

COMMENT ON INDEX nnmbot.uq_wf_community_worldstate_upgrade_typ_src_record_48ccec3c IS '同一来源内按 record_key 和 locale 唯一，不同来源允许同键共存';


--
-- Name: wf_community_bounty_stage wf_community_bounty_stage_definition_id_fkey; Type: FK CONSTRAINT; Schema: nnmbot; Owner: -
--

ALTER TABLE ONLY nnmbot.wf_community_bounty_stage
    ADD CONSTRAINT wf_community_bounty_stage_definition_id_fkey FOREIGN KEY (definition_id) REFERENCES nnmbot.wf_community_bounty_definition(id) ON DELETE CASCADE;


--
-- PostgreSQL database dump complete
--

\unrestrict ra9eszdS9qXfHaTARXT2MkQ3PB9YOMIgOGjNz3wUYbIkbjKOzb9wsgkaTzDsyFu

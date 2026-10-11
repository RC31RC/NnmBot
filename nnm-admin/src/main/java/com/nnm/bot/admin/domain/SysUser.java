package com.nnm.bot.admin.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "sys_user")
@NamedEntityGraphs({
        @NamedEntityGraph(
                name = SysUser.GRAPH_ROLES,
                attributeNodes =
                        @NamedAttributeNode("roles")
        ),
        @NamedEntityGraph(
                name = SysUser.GRAPH_AUTHORITIES,
                attributeNodes =
                        @NamedAttributeNode(value = "roles", subgraph = "roleAuthorities"),
                subgraphs =
                        @NamedSubgraph(
                        name = "roleAuthorities",
                        attributeNodes = {
                                @NamedAttributeNode("permissions"),
                                @NamedAttributeNode("menus")
                        }
                )
        )
})
public class SysUser {

    public static final String GRAPH_ROLES = "SysUser.roles";
    public static final String GRAPH_AUTHORITIES = "SysUser.authorities";

    /** 数据库主键。 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 后台登录用户名。 */
    @Column(nullable = false, unique = true, length = 50)
    private String username;

    /** BCrypt 哈希后的登录密码。 */
    @Column(nullable = false)
    private String password;

    /** 是否允许登录和使用后台。 */
    @Column(nullable = false)
    private boolean enabled = true;

    /** 用户昵称。 */
    @Column(length = 100)
    private String nickname;

    /** 联系电话。 */
    @Column(length = 30)
    private String phone;

    /** 联系邮箱。 */
    @Column(length = 120)
    private String email;

    /** 性别标识。 */
    @Column(length = 20)
    private String gender;

    /** 头像地址。 */
    @Column(length = 500)
    private String avatar;

    /** 用户被授予的系统角色。 */
    @ManyToMany
    @JoinTable(
            name = "sys_user_role",
            joinColumns =
                    @JoinColumn(name = "user_id"),
            inverseJoinColumns =
                    @JoinColumn(name = "role_id")
    )
    private Set<SysRole> roles = new LinkedHashSet<>();

    /** 创建时间。 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /** 最后更新时间。 */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

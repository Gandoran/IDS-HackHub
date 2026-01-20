package org.example.idshackhub.model.user.role;

import org.example.idshackhub.model.user.role.permission.Permission;

import java.util.List;

public interface Role {
    List<Permission> getListPermission();
    boolean hasPermission(Permission permission);
}

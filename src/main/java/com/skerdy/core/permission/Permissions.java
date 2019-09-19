package com.skerdy.core.permission;

public enum Permissions {

    READ("PERMISSION_READ", "Can read Resources!", true),
    WRITE("PERMISSION_WRITE", "Can write Resources!", true),
    EDIT("PERMISSION_EDIT", "Can edit Resources!", true),
    DELETE("PERMISSION_DELETE", "Can delete Resources!", true),
    RESOURCE_ALL("RESOURCE_ALL", "Can operate CRUDS on the specified Resource", true),
    ALL("PERMISSION_ALL","Basicly is Administrator for bussiness", false),
    SYSTEM_ADMIN("SYSTEM_ADMIN", "Has universal rights!", false);

    private final String name;
    private final String description;
    private final Boolean isResourcePermission;


    Permissions(String name, String description, Boolean isResourcePermission) {
        this.name = name;
        this.description = description;
        this.isResourcePermission = isResourcePermission;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Boolean getResourcePermission() {
        return isResourcePermission;
    }
}

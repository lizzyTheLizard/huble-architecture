package site.gutschi.humble.spring.users.model;

public enum ProjectRoleType {
    ADMIN,
    DEVELOPER,
    STAKEHOLDER,
    ;

    public boolean canRead() {
        return true;
    }

    public boolean canWrite() {
        return this == ADMIN || this == DEVELOPER;
    }

    public boolean canManage() {
        return this == ADMIN;
    }
}

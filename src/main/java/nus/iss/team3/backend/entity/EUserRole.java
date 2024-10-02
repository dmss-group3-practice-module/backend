package nus.iss.team3.backend.entity;

/**
 * User role
 *
 * @author RENJIARUI
 */
public enum EUserRole {
    ADMIN(1),
    USER(2);

    public final int code;

    EUserRole(int code) {
        this.code = code;
    }

    public static EUserRole valueOfCode(int code) {
        for (EUserRole e : values()) {
            if (e.code == code) {
                return e;
            }
        }
        return null;
    }
}

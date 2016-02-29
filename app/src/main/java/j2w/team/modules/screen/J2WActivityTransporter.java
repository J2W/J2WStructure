package j2w.team.modules.screen;

import java.util.ArrayList;

/**
 * @创建人 sky
 * @创建时间 16/2/27
 * @类描述
 */
public class J2WActivityTransporter {

    private Class<?> toClazz;
    private ArrayList<J2WActivityExtra> extras;

    public J2WActivityTransporter(Class<?> toClazz) {
        this.toClazz = toClazz;
    }

    /**
     * It is only possible to send strings as extra.
     */
    public J2WActivityTransporter addExtra(String key, String value) {
        if (extras == null)
            extras = new ArrayList<>();

        extras.add(new J2WActivityExtra(key, value));
        return this;
    }

    public Class<?> toClazz() {
        return toClazz;
    }

    public ArrayList<J2WActivityExtra> getExtras() {
        return extras;
    }
}

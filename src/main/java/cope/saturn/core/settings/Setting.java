package cope.saturn.core.settings;

import java.util.ArrayList;

@SuppressWarnings("rawtypes")
public class Setting<T> {
    private final String name;

    private final Setting parent;
    private final ArrayList<Setting> children = new ArrayList<>();

    private T value;

    private final Number min, max;

    public Setting(String name, T value) {
        this(null, name, value, null, null);
    }

    public Setting(Setting parent, String name, T value) {
        this(parent, name, value, null, null);
    }

    public Setting(String name, T value, Number min, Number max) {
        this(null, name, value, min, max);
    }

    public Setting(Setting parent, String name, T value, Number min, Number max) {
        this.parent = parent;
        this.name = name;
        this.value = value;
        this.min = min;
        this.max = max;

        if (parent != null) {
            parent.children.add(this);
        }
    }

    public String getName() {
        return name;
    }

    public Setting getParent() {
        return parent;
    }

    public ArrayList<Setting> getChildren() {
        return children;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public Number getMin() {
        return min;
    }

    public Number getMax() {
        return max;
    }
}

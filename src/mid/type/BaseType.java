package mid.type;

import java.util.Locale;

public class BaseType implements Type{
    Tag tag;

    public BaseType(Tag tag) {
        this.tag = tag;
    }

    public static enum Tag {
        I32,
        I1,
        VOID,
    }

    public Type getInnerType(){
        return null;
    }

    @Override
    public String toString() {
        return tag.name().toLowerCase(Locale.ROOT);
    }

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}

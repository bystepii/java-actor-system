package actors;

public interface ProcessModifiable {
    void addModifier(Modifier modifier);

    void removeModifier(Modifier modifier);
}

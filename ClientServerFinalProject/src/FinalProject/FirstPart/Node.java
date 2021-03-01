package FinalProject.FirstPart;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface Node extends HasUUID {
    Collection<Node> getCollection (@NotNull final Class <? extends HasUUID> desiredClass);
}

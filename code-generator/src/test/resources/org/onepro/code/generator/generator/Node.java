import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class Node {

    /**
     * Identifier
     */
    private final Long id;

    private final String title;

    private Node(
        @Nonnull Long id,
        @Nullable String title
    ) {
        this.id = requireNonNull(id, "id");
        this.title = title;
    }

    @Nonnull
    public Long getId() {
        return id;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null || obj.getClass() != getClass()) {
            return false;
        }
        Node other = (Node) obj;
        return Objects.equals(id, other.id)
            && Objects.equals(title, other.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title);
    }

    @Nonnull
    @Override
    public String toString() {
        return "Node{" +
            "id=" + id +
            ", title=" + title +
            '}';
    }

    public static class Builder {

        private Long id;
        private String title;

        private Builder() {
        }

        @Nonnull
        public static Builder node() {
            return new Builder();
        }

        @Nonnull
        public Builder id(@Nonnull Long id) {
            this.id = id;
            return this;
        }

        @Nonnull
        public Builder title(@Nullable String title) {
            this.title = title;
            return this;
        }

        @Nonnull
        public Node build() {
            return new Node(
                id,
                title
            );
        }

    }

}

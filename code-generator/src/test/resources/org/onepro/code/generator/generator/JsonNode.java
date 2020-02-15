import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Objects.requireNonNull;

public class JsonNode {

    /**
     * Identifier
     */
    private final Long id;

    private final String title;

    @JsonCreator
    private JsonNode(
        @JsonProperty("id") @Nonnull Long id,
        @JsonProperty("title") @Nullable String title
    ) {
        this.id = id;
        this.title = title;
    }

    @JsonProperty("id")
    @Nonnull
    public Long getId() {
        return id;
    }

    @JsonProperty("title")
    @Nullable
    public String getTitle() {
        return title;
    }

    @Nonnull
    @Override
    public String toString() {
        return "JsonNode{" +
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
        public static Builder jsonNode() {
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
        public JsonNode build() {
            return new JsonNode(
                requireNonNull(id, "id"),
                title
            );
        }

    }

}

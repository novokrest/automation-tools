package org.onepro.code.generator.generator;

;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class Node {

private final Long id;

private final String title;

private final Node parent;

private Node(
@Nonnull Long id,
@Nonnull String title,
@Nullable Node parent
) {
this.id = id;
this.title = title;
this.parent = parent;
}

@Nonnull
public Long getId() {
return id;
}

@Nonnull
public String getTitle() {
return title;
}

@Nonnull
public Optional<Node> getParent() {
return Optional.ofNullable(parent);
}

@Nonnull
public static Builder builder() {
return new Builder();
}

@Nonnull
public static Builder builder(@Nonnull Node copy) {
Builder builder = new Builder();
builder.id = copy.id;
builder.title = copy.title;
builder.parent = copy.parent;
return builder;
}

public static class Builder {

private Long id;
private String title;
private Node parent;

private Builder() {
}

@Nonnull
public Builder withId(@Nonnull Long id) {
this.id = id;
return this;
}

@Nonnull
public Builder withTitle(@Nonnull String title) {
this.title = title;
return this;
}

@Nullable
public Builder withParent(@Nullable Node parent) {
this.parent = parent;
return this;
}

@Nonnull
public Node build() {
return new Node(
id,
title,
parent
);
}

}

}

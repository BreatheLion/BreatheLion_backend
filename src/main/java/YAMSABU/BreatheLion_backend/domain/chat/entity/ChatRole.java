package YAMSABU.BreatheLion_backend.domain.chat.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ChatRole {
    @JsonProperty("user")
    user,
    @JsonProperty("assistant")
    assistant
}

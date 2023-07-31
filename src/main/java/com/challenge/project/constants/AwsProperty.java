package com.challenge.project.constants;

import lombok.Getter;

@Getter
public enum AwsProperty {

    PROP_IMAGE_DIR_NAME("prop/image"),
    ZIP_DIR_NAME("zip/prop");

    private final String name;

    AwsProperty(String name) {
        this.name = name;
    }
}

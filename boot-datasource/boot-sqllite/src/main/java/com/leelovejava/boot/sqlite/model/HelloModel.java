package com.leelovejava.boot.sqlite.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author leelovejava
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HelloModel {
    private long id;
    private String title;
    private String text;

    public HelloModel(String title, String text) {
        this.title = title;
        this.text = text;
    }
}

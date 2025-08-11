package com.smartStudy.dto;

public class ExcerciseDTO {
    private Integer id;
    private String title;
    private String description;
    private String type;
    private  Integer createBy;
    private ChapterDTO chapter;

    public ExcerciseDTO(Integer id, String title, String description, String type, Integer createBy, ChapterDTO chapter)
    {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.createBy = createBy;
        this.chapter = chapter;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public ChapterDTO getChapter() {
        return chapter;
    }

    public void setChapter(ChapterDTO chapter) {
        this.chapter = chapter;
    }
}

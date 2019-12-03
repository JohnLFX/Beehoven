package dev.roundtable.beehoven.objects;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Javabean for a Project
 */
public class Project implements Serializable {

    private int id;
    public String name;
    private String title;
    private String subtitle;
    private String artist;
    private String album;
    private String wordsBy;
    private String musicBy;
    private List<String> sharedWith = new ArrayList<>();

    public Project() {
    }

    public List<String> getSharedWith() {
        return sharedWith;
    }

    public void setSharedWith(List<String> sharedWith) {
        this.sharedWith = sharedWith;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Nullable
    public String getName() {
        return name;
    }

    public void setName(String name) {
        Objects.requireNonNull(name);
        this.name = name;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        Objects.requireNonNull(title);
        this.title = title;
    }

    @Nullable
    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    @Nullable
    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Nullable
    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    @Nullable
    public String getWordsBy() {
        return wordsBy;
    }

    public void setWordsBy(String wordsBy) {
        this.wordsBy = wordsBy;
    }

    @Nullable
    public String getMusicBy() {
        return musicBy;
    }

    public void setMusicBy(String musicBy) {
        this.musicBy = musicBy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return id == project.id &&
                Objects.equals(name, project.name) &&
                Objects.equals(title, project.title) &&
                Objects.equals(subtitle, project.subtitle) &&
                Objects.equals(artist, project.artist) &&
                Objects.equals(album, project.album) &&
                Objects.equals(wordsBy, project.wordsBy) &&
                Objects.equals(musicBy, project.musicBy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, title, subtitle, artist, album, wordsBy, musicBy);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                ", wordsBy='" + wordsBy + '\'' +
                ", musicBy='" + musicBy + '\'' +
                '}';
    }
}

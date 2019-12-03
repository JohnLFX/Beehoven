package objects;

import dev.roundtable.beehoven.objects.Project;
import org.apache.commons.text.RandomStringGenerator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import testing.TestUtils;

import java.util.concurrent.ThreadLocalRandom;

public class TestProject {

    private static final RandomStringGenerator STRING_GENERATOR;

    static {
        STRING_GENERATOR = new RandomStringGenerator.Builder().withinRange('a', 'z').build();
    }

    private Project project;

    @BeforeClass
    public void setup() {
        this.project = new Project();
    }

    @Test
    public void testID() {
        final int id = ThreadLocalRandom.current().nextInt();
        project.setId(id);
        Assert.assertEquals(id, project.getId());
    }

    @Test
    public void testName() {
        final String name = STRING_GENERATOR.generate(15);
        project.setName(name);
        Assert.assertEquals(project.getName(), name);
    }

    @Test
    public void testTitle() {
        final String title = STRING_GENERATOR.generate(15);
        project.setTitle(title);
        Assert.assertEquals(project.getTitle(), title);
    }

    @Test
    public void testSubtitle() {
        final String subtitle = STRING_GENERATOR.generate(15);
        project.setSubtitle(subtitle);
        Assert.assertEquals(project.getSubtitle(), subtitle);
    }

    @Test
    public void testArtist() {
        final String artist = STRING_GENERATOR.generate(15);
        project.setArtist(artist);
        Assert.assertEquals(project.getArtist(), artist);
    }

    @Test
    public void testAlbum() {
        final String album = STRING_GENERATOR.generate(15);
        project.setAlbum(album);
        Assert.assertEquals(project.getAlbum(), album);
    }

    @Test
    public void testWordsBy() {
        final String wordsBy = STRING_GENERATOR.generate(15);
        project.setWordsBy(wordsBy);
        Assert.assertEquals(project.getWordsBy(), wordsBy);
    }

    @Test
    public void testMusicBy() {
        final String musicBy = STRING_GENERATOR.generate(15);
        project.setMusicBy(musicBy);
        Assert.assertEquals(project.getMusicBy(), musicBy);
    }

    @Test
    public void testEquals() {

        Project project1 = TestUtils.randomProject();

        // Copy the project
        Project project2 = new Project();
        project2.setId(project1.getId());
        project2.setName(project1.getName());
        project2.setTitle(project1.getTitle());
        project2.setSubtitle(project1.getSubtitle());
        project2.setArtist(project1.getArtist());
        project2.setAlbum(project1.getAlbum());
        project2.setMusicBy(project1.getMusicBy());
        project2.setWordsBy(project1.getWordsBy());

        Assert.assertEquals(project2, project1, "The exact same project is not equal");
        Assert.assertEquals(project2.hashCode(), project1.hashCode(), "Hash code is not equal though it is not the same project");

    }

}

package sa.ksu.swe444.hackwati;


public class Item {
    private String title;
    private String userId;
    private String image;
    private String description;
    private String rate;
    private String sound;

    private int channelImage;
    private int soundImage;


    public Item(String title, String image, String description, String rate , String sound, String userId) {
        this.title = title;
        this.image = image;
        this.userId=userId;
        this.description=description;
        this.rate=rate;
        this.sound=sound;
    }

    public Item(String title, String image, String userId) {
        this.title = title;
        this.image = image;
        this.userId=userId;

    }

    public Item(String title, int image, int channelImage, String views, String channelName, int soundImage) {
        this.title = title;
        this.image = channelName;
        this.channelImage = channelImage;
        this.soundImage = soundImage;
        this.rate = views;
        this.title=channelName;
    }

    public Item(String title, int image) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
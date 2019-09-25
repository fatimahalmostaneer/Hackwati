package sa.ksu.swe444.hackwati;


public class Item {
    private String title;
    private String channelName;
    private int image;
    private int channelImage;

    public Item(String title, int image, int channelImage, String views,String channelName,int soundImage) {
        this.title = title;
        this.image = image;
        this.channelImage = channelImage;
        this.channelName=channelName;
    }

    public Item(String title, int image) {
        this.title = title;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getChannelImage() {
        return channelImage;
    }

    public void setChannelImage(int channelImage) {
        this.channelImage = channelImage;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }
}
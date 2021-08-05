package my.service.resource;

public class CustomUpload {

  String image1;
  String image2;

  public CustomUpload() {
  }

  public CustomUpload(String image1, String image2) {
    this.image1 = image1;
    this.image2 = image2;
  }

  public String getImage1() {
    return image1;
  }

  public String getImage2() {
    return image2;
  }
}

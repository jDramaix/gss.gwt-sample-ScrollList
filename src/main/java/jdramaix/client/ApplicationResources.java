package jdramaix.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource.ClassName;
import com.google.gwt.resources.client.GssResource;
import com.google.gwt.resources.client.ImageResource;

public interface ApplicationResources extends ClientBundle {
  public interface Style extends GssResource {
    String future();

    String item();

    String pager();

    String cards();

    String grow();

    String flip();

    String fly();

    @ClassName("fly-reverse")
    String flyReverse();

    String skew();

    String helix();

    String wave();

    String fan();

    String tilt();

    String curl();

    String papercut();

    String fade();

    String twirl();

  }

  ImageResource contact();

  @Source("Application.gss")
  Style style();
}

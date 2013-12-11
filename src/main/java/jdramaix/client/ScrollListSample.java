package jdramaix.client;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.text.shared.AbstractRenderer;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.Widget;
import jdramaix.client.ContactDatabase.ContactInfo;

import java.util.Arrays;

import static com.google.gwt.query.client.GQuery.$;


public class ScrollListSample implements EntryPoint {
  /**
   * The Cell used to render a {@link ContactInfo}. Code coming from the GWT
   * showcase
   *
   */
  private static class ContactCell extends AbstractCell<ContactInfo> {

    /**
     * The html of the image used for contacts.
     *
     */
    private final String imageHtml;

    public ContactCell(ImageResource image) {
      this.imageHtml = AbstractImagePrototype.create(image).getHTML();
    }

    @Override
    public void render(Context ctx, ContactInfo value, SafeHtmlBuilder sb) {
      // Value can be null, so do a null check..
      if (value == null) {
        return;
      }

      String classNames = resources.style().item() ;

      sb.appendHtmlConstant("<div class=\"" + classNames + " \">");
      sb.appendHtmlConstant("<table>");

      // Add the contact image.
      sb.appendHtmlConstant("<tr><td rowspan='3'><div>");
      sb.appendHtmlConstant(imageHtml);
      sb.appendHtmlConstant("</div></td>");

      // Add the name and address.
      sb.appendHtmlConstant("<td style='font-size:95%;'>");
      sb.appendEscaped(value.getFullName());
      sb.appendHtmlConstant("</td></tr><tr><td>");
      sb.appendEscaped(value.getAddress());
      sb.appendHtmlConstant("</td></tr></table></div>");
    }
  }

  private static enum Effect {
    NONE {
      String getEffectStyle() {
        return null;
      }
    },
    CARDS {
      String getEffectStyle() {
        return resources.style().cards();
      }
    }, GROW {
      String getEffectStyle() {
        return resources.style().grow();
      }
    }, FLIP {
      String getEffectStyle() {
        return resources.style().flip();
      }
    }, FLY {
      String getEffectStyle() {
        return resources.style().fly();
      }
    }, FLY_REVERSE {
      String getEffectStyle() {
        return resources.style().flyReverse();
      }
    }, SKEW {
      String getEffectStyle() {
        return resources.style().skew();
      }
    }, HELIX {
      String getEffectStyle() {
        return resources.style().helix();
      }
    }, WAVE {
      String getEffectStyle() {
        return resources.style().wave();
      }
    }, FAN {
      String getEffectStyle() {
        return resources.style().fan();
      }
    }, TILT {
      String getEffectStyle() {
        return resources.style().tilt();
      }
    }, CURL {
      String getEffectStyle() {
        return resources.style().curl();
      }
    }, PAPERCUT {
      String getEffectStyle() {
        return resources.style().papercut();
      }
    }, FADE {
      String getEffectStyle() {
        return resources.style().fade();
      }
    }, TWIRL {
      String getEffectStyle() {
        return resources.style().twirl();
      }
    };

    abstract String getEffectStyle();
  }

  private static ApplicationResources resources = GWT.create(ApplicationResources.class);

  private Effect currentEffect = Effect.NONE;
  private Element effectContainer;

  public void onModuleLoad() {
    resources.style().ensureInjected();

    RootPanel.get("effectList").add(createEffectSelector());
    RootPanel.get().add(createList());

    effectContainer = $("." + resources.style().pager()).elements()[0];
  }

  private Widget createEffectSelector() {
    ValueListBox<Effect> listBox = new ValueListBox<Effect>(new AbstractRenderer<Effect>() {
      @Override
      public String render(Effect object) {
        return object != null ? object.name().toLowerCase() : "";
      }
    });

    listBox.setValue(currentEffect);
    listBox.setAcceptableValues(Arrays.asList(Effect.values()));
    listBox.addValueChangeHandler(new ValueChangeHandler<Effect>() {
      @Override
      public void onValueChange(ValueChangeEvent<Effect> event) {
        Effect effect = event.getValue();

        if (currentEffect != Effect.NONE) {
          effectContainer.removeClassName(currentEffect.getEffectStyle());
        }

        if (effect != Effect.NONE) {
          effectContainer.addClassName(effect.getEffectStyle());
        }

        currentEffect = effect;
      }
    });
    return listBox;
  }

  private Widget createList() {

    ContactCell contactCell = new ContactCell(resources.contact());

    CellList<ContactInfo> cellList = new CellList<ContactInfo>(contactCell, ContactDatabase.ContactInfo.KEY_PROVIDER);

    cellList.setPageSize(30);

    ContactDatabase.get().addDataDisplay(cellList);

    ShowMorePagerPanel pagerPanel = new ShowMorePagerPanel();
    pagerPanel.setDisplay(cellList);

    return pagerPanel;
  }
}

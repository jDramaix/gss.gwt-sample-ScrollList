/**
 * Copyright 2011 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package jdramaix.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.event.logical.shared.AttachEvent.Handler;
import com.google.gwt.query.client.GQuery;
import com.google.gwt.query.client.js.JsObjectArray;
import com.google.gwt.user.cellview.client.AbstractPager;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.HasRows;
import com.google.gwt.view.client.RangeChangeEvent;

import static com.google.gwt.query.client.GQuery.$;

/**
 * A scrolling pager that automatically increases the range every time the
 * scroll bar reaches the bottom.
 * 
 * Code coming from GWT showcase
 */
public class ShowMorePagerPanel extends AbstractPager {

  /**
   * The default increment size.
   */
  private static final int DEFAULT_INCREMENT = 20;

  private static ApplicationResources resources = GWT.create(ApplicationResources.class);

  /**
   * The increment size.
   */
  private int incrementSize = DEFAULT_INCREMENT;

  /**
   * The last scroll position.
   */
  private int lastScrollPos = 0;

  /**
   * The scrollable panel.
   */
  private final ScrollPanel scrollable = new ScrollPanel();

  /**
   * Construct a new {@link ShowMorePagerPanel}.
   */
  public ShowMorePagerPanel() {
    init();

    addAttachHandler(new Handler() {
      @Override
      public void onAttachOrDetach(AttachEvent event) {
        if (event.isAttached()) {
          Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
              synchronize();
              getDisplay().addRangeChangeHandler(new RangeChangeEvent.Handler() {
                @Override
                public void onRangeChange(RangeChangeEvent event) {
                  GWT.log("synchronize " + event.getNewRange());
                  Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                      synchronize();
                    }
                  });
                }
              });
            }
          });
        }
      }
    });



    // Handle scroll events.
    scrollable.addScrollHandler(new ScrollHandler() {
      public void onScroll(ScrollEvent event) {
        update();
        // If scrolling up, ignore the event.
        int oldScrollPos = lastScrollPos;
        lastScrollPos = scrollable.getVerticalScrollPosition();
        if (oldScrollPos >= lastScrollPos) {
          return;
        }

        HasRows display = getDisplay();
        if (display == null) {
          return;
        }
        int maxScrollTop = scrollable.getWidget().getOffsetHeight()
            - scrollable.getOffsetHeight() - 40;
        if (lastScrollPos >= maxScrollTop) {
          // We are near the end, so increase the page size.
          int newPageSize = Math.min(display.getVisibleRange().getLength()
              + incrementSize, display.getRowCount());
          display.setVisibleRange(0, newPageSize);
        }
      }
    });
  }

  private GQuery $scrollable;

  private void synchronize() {
    if ($scrollable == null) {
      $scrollable = $(scrollable);
    }
    int currentTopScroll = scrollable.getVerticalScrollPosition();

    GQuery items = $("." + resources.style().item(), scrollable);


    for (Element e : items.elements() ) {
      GQuery element = $(e);
      int elementTop = element.offset().top + currentTopScroll;
      element.data("___offsetTop", elementTop);
    }
  }

  private void update() {
    int currentTopScroll = scrollable.getVerticalScrollPosition();
    int bottomScroll = $scrollable.offset().top + $scrollable.innerHeight() + currentTopScroll;

    JsObjectArray<GQuery> pastElements =  JsObjectArray.create();
    JsObjectArray<GQuery> futureElements =  JsObjectArray.create();

      // update current elements
      GQuery pastElement = $("." + resources.style().item()+ ":not(." + resources.style().future()
          + ")", scrollable);
      for (int i = pastElement.length() - 1; i > 0; i--) {
        GQuery element = pastElement.eq(i);

        if (isPast(element, bottomScroll)) {
          break;
        } else {
          futureElements.add(element);
        }
      }

      GQuery futureElement = $("." + resources.style().future(), scrollable);
      for (int i = 0; i < futureElement.length(); i++) {
        GQuery element = futureElement.eq(i);
        if (isPast(element, bottomScroll)) {
          pastElements.add(element);
        } else {
          break;
        }
      }


    // avoid browser reflows
    for (int i = 0; i < pastElements.length(); i++) {
      pastElements.get(i).removeClass(resources.style().future());
    }

    for (int i = 0; i < futureElements.length(); i++) {
      futureElements.get(i).addClass(resources.style().future());
    }
  }

  private boolean isPast(GQuery element, int bottomScroll) {
    Integer offsetTop = element.data("___offsetTop", Integer.class);
    if (offsetTop == null) {
      return false;
    }
    return  offsetTop < bottomScroll;
  }

  /**
   * Get the number of rows by which the range is increased when the scrollbar
   * reaches the bottom.
   * 
   * @return the increment size
   */
  public int getIncrementSize() {
    return incrementSize;
  }

  @Override
  public void setDisplay(HasRows display) {
    assert display instanceof Widget : "display must extend Widget";
    scrollable.setWidget((Widget) display);
    super.setDisplay(display);
  }

  /**
   * Set the number of rows by which the range is increased when the scrollbar
   * reaches the bottom.
   * 
   * @param incrementSize
   *          the incremental number of rows
   */
  public void setIncrementSize(int incrementSize) {
    this.incrementSize = incrementSize;
  }

  @Override
  protected void onRangeOrRowCountChanged() {
  }

  private void init() {
    FlowPanel p = new FlowPanel();
    p.add(new Label("Contacts"));
    p.add(scrollable);
    scrollable.addStyleName(resources.style().pager());
    initWidget(p);
  }

}

#Header


## Using

First, add gradle dependency into your build.gradle:
```groovy
dependencies {
    compile 'com.cleveroad:bubbleanimation-layout:1.0.0'
}
``` 

Then you can declare it in you layout file like this:
```xml
<com.cleveroad.bubbleanimation.BubbleAnimationLayout
    android:id="@+id/animation_view"
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@android:color/darker_gray"
    android:paddingBottom="@dimen/padding_s"
    android:paddingEnd="@dimen/padding_m"
    android:paddingStart="@dimen/padding_m"
    app:bav_animation_color="@color/base_red"
    app:bav_indicator_width="@dimen/indicator_width"
    >

    <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="@dimen/list_item_size"
        android:background="@android:color/white"
        app:bav_view_type="base_container"
        >
        ...
    </RelativeLayout>
    
    <RelativeLayout
        android:id="@+id/fl_context_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:bav_view_type="context_container"
        >
        ...
    </RelativeLayout>
    
</com.cleveroad.bubbleanimation.BubbleAnimationLayout>
```

BubbleAnimationLayout can contain only two children. One of them is base container (you can declare it with attribute app:bav_view_type="base_container") and another one is context container (you can declare it with attribute app:bav_view_type="context_container").
**Be careful**, layout calculates size by base container's layout params

## Customization
### Setup indicator and bubble animation color
You can change animation color with attribute 'app:bav_animation_color'
```xml
<com.cleveroad.bubbleanimation.BubbleAnimationLayout
    ...
    app:bav_animation_color="#b92714"
    >
```

or 
```java
 BubbleAnimationLayout mBalBaseView = ...;
 mBalBaseView.setAnimationColor(Color.YELLOW);
```

### Setup indicator width
You can change indicator width with attribute 'app:bav_indicator_width'
```xml
<com.cleveroad.bubbleanimation.BubbleAnimationLayout
    ...
    app:bav_indicator_width="10dp"
    >
```

or
```java
 BubbleAnimationLayout mBalBaseView = ...;
 mBalBaseView.setIndicatorWidth(30);
```

### Show context container
You can show context container with animation (base container will be hidden)
```java
 BubbleAnimationLayout mBalBaseView = ...;
 View contextView = findViewById(R.id.fl_context_view);
 Animator animator = ObjectAnimator.ofPropertyValuesHolder(contextView, PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f))
                    .setDuration(500);
 mBalBaseView.showContextViewWithAnimation(animator);
```

or without animation
```java
 BubbleAnimationLayout mBalBaseView = ...;
 mBalBaseView.showContextView();
```

### Show base container
For displaying base container with animation call (context container and bubble view will be hidden)
```java
 BubbleAnimationLayout mBalBaseView = ...;
 View contextView = findViewById(R.id.fl_context_view);
 Animator animator = ObjectAnimator.ofPropertyValuesHolder(contextView, PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f))
                    .setDuration(500);
 mBalBaseView.showBaseViewWithAnimation(animator);
```

or without animation
```java
 BubbleAnimationLayout mBalBaseView = ...;
 mBalBaseView.showBaseView();
```

### Show/hide bubble view
You can show bubble view with animation (for handling ending of animation specify [BubbleAnimationEndListener])
```java
 BubbleAnimationLayout mBalBaseView = ...;
 mBalBaseView.showBubbleViewWithAnimation(new BubbleAnimationLayout.BubbleAnimationEndListener() {
     @Override
     public void onEndAnimation(boolean isForwardAnimation, Animator animation) {
        //Do something
     }
 });
```

or without animation
```java
 BubbleAnimationLayout mBalBaseView = ...;
 mBalBaseView.showBubbledView();
```

For hiding with animation (for handling ending of animation specify [BubbleAnimationEndListener]) call
```java
 BubbleAnimationLayout mBalBaseView = ...;
 mBalBaseView.hideBubbledViewWithAnimation(new BubbleAnimationLayout.BubbleAnimationEndListener() {
     @Override
     public void onEndAnimation(boolean isForwardAnimation, Animator animation) {
        //Do something
     }
 });
```

or without animation
```java
 BubbleAnimationLayout mBalBaseView = ...;
 mBalBaseView.hideBubbledView();
```

### Reset view
Call [BubbleAnimationLayout#resetView()] to reset view to initial state
```java
 BubbleAnimationLayout mBalBaseView = ...;
 mBalBaseView.resetView();
```

### Handling ending of animation
For handling animation's ending declare [BubbleAnimationEndListener]
```java
 BubbleAnimationLayout mBalBaseView = ...;
 mBalBaseView.addAnimationEndListener(new BubbleAnimationLayout.BubbleAnimationEndListener() {
     @Override
     public void onEndAnimation(boolean isForwardAnimation, Animator animation) {
         //Do something
     }
 });
```

### Usage in the list item
If you call [BubbleAnimationLayout#hideBubbledViewWithAnimation(BubbleAnimationEndListener)] and scroll animation will show in recycled view.
E.g. you can lock scroll while animation will end for resolving this issue.
```java
 private static class NonScrollLinearLayoutManager extends LinearLayoutManager {
 
         private boolean mCanScroll = true;
 
         public NonScrollLinearLayoutManager(Context context) {
             super(context);
         }
 
         @SuppressWarnings("unused")
         public NonScrollLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
             super(context, orientation, reverseLayout);
         }
 
         @SuppressWarnings("unused")
         public NonScrollLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
             super(context, attrs, defStyleAttr, defStyleRes);
         }
 
         @Override
         public boolean canScrollHorizontally() {
             return super.canScrollHorizontally() && mCanScroll;
         }
 
         @Override
         public boolean canScrollVertically() {
             return super.canScrollVertically() && mCanScroll;
         }
 }
 
 {
    ...
    RecyclerView rvUsers = ...;
    rvUsers.setHasFixedSize(true);
    final NonScrollLinearLayoutManager layoutManager = new NonScrollLinearLayoutManager(this);
    rvUsers.setLayoutManager(layoutManager);
    layoutManager.mCanScroll = false;   
    ...
 }
```

## Support
If you have any questions regarding the use of this tutorial, please contact us for support
at info@cleveroad.com (email subject: «Sliding android app tutorial. Support request.»)
<br>or
<br>Use our contacts:
<br><a href="https://www.cleveroad.com/?utm_source=github&utm_medium=link&utm_campaign=contacts">Cleveroad.com</a>
<br><a href="https://www.facebook.com/cleveroadinc">Facebook account</a>
<br><a href="https://twitter.com/CleveroadInc">Twitter account</a>
<br><a href="https://plus.google.com/+CleveroadInc/">Google+ account</a>

## License


        The MIT License (MIT)

        Copyright (c) 2015-2016 Cleveroad

        Permission is hereby granted, free of charge, to any person obtaining a copy
        of this software and associated documentation files (the "Software"), to deal
        in the Software without restriction, including without limitation the rights
        to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
        copies of the Software, and to permit persons to whom the Software is
        furnished to do so, subject to the following conditions:

        The above copyright notice and this permission notice shall be included in all
        copies or substantial portions of the Software.

        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
        IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
        FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
        AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
        LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
        OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
        SOFTWARE.
        
[BubbleAnimationLayout#resetView()]: https://github.com/Cleveroad/BubbleAnimationLayout/blob/develop/library/src/main/java/com/cleveroad/bubbleanimation/BubbleAnimationLayout.java#L352
[BubbleAnimationLayout#hideBubbledViewWithAnimation(BubbleAnimationEndListener)]: https://github.com/Cleveroad/BubbleAnimationLayout/blob/develop/library/src/main/java/com/cleveroad/bubbleanimation/BubbleAnimationLayout.java#L404
[BubbleAnimationEndListener]: https://github.com/Cleveroad/BubbleAnimationLayout/blob/develop/library/src/main/java/com/cleveroad/bubbleanimation/BubbleAnimationLayout.java#L687


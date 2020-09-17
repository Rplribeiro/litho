/*
 * Copyright (c) Facebook, Inc. and its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.facebook.litho;

import static androidx.core.view.ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_AUTO;
import static androidx.test.core.app.ApplicationProvider.getApplicationContext;
import static com.facebook.litho.Layout.createAndMeasureComponent;
import static com.facebook.litho.SizeSpec.EXACTLY;
import static com.facebook.litho.SizeSpec.UNSPECIFIED;
import static com.facebook.litho.SizeSpec.makeSizeSpec;
import static com.facebook.litho.it.R.drawable.background_with_padding;
import static com.facebook.litho.it.R.drawable.background_without_padding;
import static com.facebook.litho.testing.Whitebox.getInternalState;
import static com.facebook.yoga.YogaAlign.STRETCH;
import static com.facebook.yoga.YogaDirection.INHERIT;
import static com.facebook.yoga.YogaDirection.RTL;
import static com.facebook.yoga.YogaEdge.ALL;
import static com.facebook.yoga.YogaEdge.BOTTOM;
import static com.facebook.yoga.YogaEdge.LEFT;
import static com.facebook.yoga.YogaEdge.RIGHT;
import static com.facebook.yoga.YogaEdge.TOP;
import static com.facebook.yoga.YogaPositionType.ABSOLUTE;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Pair;
import com.facebook.litho.testing.Whitebox;
import com.facebook.litho.testing.logging.TestComponentsReporter;
import com.facebook.litho.testing.testrunner.LithoTestRunner;
import com.facebook.litho.widget.SolidColor;
import com.facebook.litho.widget.Text;
import com.facebook.yoga.YogaAlign;
import com.facebook.yoga.YogaNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(LithoTestRunner.class)
public class InternalNodeTest {
  private static final int LIFECYCLE_TEST_ID = 1;

  private static class TestComponent extends Component {

    protected TestComponent() {
      super("TestComponent");
    }

    @Override
    public boolean isEquivalentTo(Component other, boolean shouldCompareState) {
      return this == other;
    }

    @Override
    int getTypeId() {
      return LIFECYCLE_TEST_ID;
    }
  }

  private static InternalNode acquireInternalNode() {
    final ComponentContext context = new ComponentContext(getApplicationContext());
    context.setLayoutStateContextForTesting();

    return createAndMeasureComponent(
        context,
        Column.create(context).build(),
        makeSizeSpec(0, UNSPECIFIED),
        makeSizeSpec(0, UNSPECIFIED));
  }

  private static InternalNode acquireInternalNodeWithLogger(ComponentsLogger logger) {
    final ComponentContext context = new ComponentContext(getApplicationContext(), "TEST", logger);
    context.setLayoutStateContextForTesting();

    return createAndMeasureComponent(
        context,
        Column.create(context).build(),
        makeSizeSpec(0, UNSPECIFIED),
        makeSizeSpec(0, UNSPECIFIED));
  }

  private final TestComponentsReporter mComponentsReporter = new TestComponentsReporter();

  @Before
  public void setup() {
    ComponentsReporter.provide(mComponentsReporter);
  }

  @Test
  public void testLayoutDirectionFlag() {
    final InternalNode node = acquireInternalNode();
    node.layoutDirection(INHERIT);
    assertThat(isFlagSet(node, "PFLAG_LAYOUT_DIRECTION_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_LAYOUT_DIRECTION_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testAlignSelfFlag() {
    final InternalNode node = acquireInternalNode();
    node.alignSelf(STRETCH);
    assertThat(isFlagSet(node, "PFLAG_ALIGN_SELF_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_ALIGN_SELF_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testPositionTypeFlag() {
    final InternalNode node = acquireInternalNode();
    node.positionType(ABSOLUTE);
    assertThat(isFlagSet(node, "PFLAG_POSITION_TYPE_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_POSITION_TYPE_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testFlexFlag() {
    final InternalNode node = acquireInternalNode();
    node.flex(1.5f);
    assertThat(isFlagSet(node, "PFLAG_FLEX_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_FLEX_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testFlexGrowFlag() {
    final InternalNode node = acquireInternalNode();
    node.flexGrow(1.5f);
    assertThat(isFlagSet(node, "PFLAG_FLEX_GROW_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_FLEX_GROW_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testFlexShrinkFlag() {
    final InternalNode node = acquireInternalNode();
    node.flexShrink(1.5f);
    assertThat(isFlagSet(node, "PFLAG_FLEX_SHRINK_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_FLEX_SHRINK_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testFlexBasisFlag() {
    final InternalNode node = acquireInternalNode();
    node.flexBasisPx(1);
    assertThat(isFlagSet(node, "PFLAG_FLEX_BASIS_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_FLEX_BASIS_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testImportantForAccessibilityFlag() {
    final InternalNode node = acquireInternalNode();
    node.importantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_AUTO);
    assertThat(isFlagSet(node, "PFLAG_IMPORTANT_FOR_ACCESSIBILITY_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_IMPORTANT_FOR_ACCESSIBILITY_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testDuplicateParentStateFlag() {
    final InternalNode node = acquireInternalNode();
    node.duplicateParentState(false);
    assertThat(isFlagSet(node, "PFLAG_DUPLICATE_PARENT_STATE_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_DUPLICATE_PARENT_STATE_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testMarginFlag() {
    final InternalNode node = acquireInternalNode();
    node.marginPx(ALL, 3);
    assertThat(isFlagSet(node, "PFLAG_MARGIN_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_MARGIN_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testPaddingFlag() {
    final InternalNode node = acquireInternalNode();
    node.paddingPx(ALL, 3);
    assertThat(isFlagSet(node, "PFLAG_PADDING_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_PADDING_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testPositionFlag() {
    final InternalNode node = acquireInternalNode();
    node.positionPx(ALL, 3);
    assertThat(isFlagSet(node, "PFLAG_POSITION_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_POSITION_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testWidthFlag() {
    final InternalNode node = acquireInternalNode();
    node.widthPx(4);
    assertThat(isFlagSet(node, "PFLAG_WIDTH_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_WIDTH_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testMinWidthFlag() {
    final InternalNode node = acquireInternalNode();
    node.minWidthPx(4);
    assertThat(isFlagSet(node, "PFLAG_MIN_WIDTH_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_MIN_WIDTH_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testMaxWidthFlag() {
    final InternalNode node = acquireInternalNode();
    node.maxWidthPx(4);
    assertThat(isFlagSet(node, "PFLAG_MAX_WIDTH_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_MAX_WIDTH_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testHeightFlag() {
    final InternalNode node = acquireInternalNode();
    node.heightPx(4);
    assertThat(isFlagSet(node, "PFLAG_HEIGHT_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_HEIGHT_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testMinHeightFlag() {
    final InternalNode node = acquireInternalNode();
    node.minHeightPx(4);
    assertThat(isFlagSet(node, "PFLAG_MIN_HEIGHT_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_MIN_HEIGHT_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testMaxHeightFlag() {
    final InternalNode node = acquireInternalNode();
    node.maxHeightPx(4);
    assertThat(isFlagSet(node, "PFLAG_MAX_HEIGHT_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_MAX_HEIGHT_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testBackgroundFlag() {
    final InternalNode node = acquireInternalNode();
    node.backgroundColor(0xFFFF0000);
    assertThat(isFlagSet(node, "PFLAG_BACKGROUND_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_BACKGROUND_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testForegroundFlag() {
    final InternalNode node = acquireInternalNode();
    node.foregroundColor(0xFFFF0000);
    assertThat(isFlagSet(node, "PFLAG_FOREGROUND_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_FOREGROUND_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testAspectRatioFlag() {
    final InternalNode node = acquireInternalNode();
    node.aspectRatio(1);
    assertThat(isFlagSet(node, "PFLAG_ASPECT_RATIO_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_ASPECT_RATIO_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void testTransitionKeyFlag() {
    final InternalNode node = acquireInternalNode();
    node.transitionKey("key", "");
    assertThat(isFlagSet(node, "PFLAG_TRANSITION_KEY_IS_SET")).isTrue();
    clearFlag(node, "PFLAG_TRANSITION_KEY_IS_SET");
    assertEmptyFlags(node);
  }

  @Test
  public void setNestedTreeDoesntTransferLayoutDirectionIfExplicitlySetOnNestedNode() {
    InternalNode holderNode = acquireInternalNode();
    InternalNode nestedTree = acquireInternalNode();

    nestedTree.layoutDirection(RTL);
    holderNode.calculateLayout();
    holderNode.setNestedTree(nestedTree);

    assertThat(isFlagSet(holderNode, "PFLAG_LAYOUT_DIRECTION_IS_SET")).isFalse();
    assertThat(holderNode.getStyleDirection()).isEqualTo(INHERIT);
    assertThat(nestedTree.getStyleDirection()).isEqualTo(RTL);
  }

  @Test
  public void testCopyIntoTrasferLayoutDirectionIfNotSetOnTheHolderOrOnTheNestedTree() {
    InternalNode holderNode = acquireInternalNode();
    InternalNode nestedTree = acquireInternalNode();

    holderNode.calculateLayout();
    holderNode.copyInto(nestedTree);

    assertThat(isFlagSet(holderNode, "PFLAG_LAYOUT_DIRECTION_IS_SET")).isFalse();
    assertThat(isFlagSet(nestedTree, "PFLAG_LAYOUT_DIRECTION_IS_SET")).isTrue();
  }

  @Test
  public void testCopyIntoNestedTreeTransferLayoutDirectionIfExplicitlySetOnHolderNode() {
    InternalNode holderNode = acquireInternalNode();
    InternalNode nestedTree = acquireInternalNode();

    holderNode.layoutDirection(RTL);
    holderNode.calculateLayout();
    holderNode.copyInto(nestedTree);

    assertThat(nestedTree.getStyleDirection()).isEqualTo(RTL);
  }

  @Test
  public void testCopyIntoNodeSetFlags() {
    InternalNode orig = acquireInternalNode();
    InternalNode dest = acquireInternalNode();

    orig.importantForAccessibility(0);
    orig.duplicateParentState(true);
    orig.background(new ColorDrawable());
    orig.foreground(null);
    orig.visibleHandler(null);
    orig.focusedHandler(null);
    orig.fullImpressionHandler(null);
    orig.invisibleHandler(null);
    orig.unfocusedHandler(null);
    orig.visibilityChangedHandler(null);

    orig.copyInto(dest);

    assertThat(isFlagSet(dest, "PFLAG_IMPORTANT_FOR_ACCESSIBILITY_IS_SET")).isTrue();
    assertThat(isFlagSet(dest, "PFLAG_DUPLICATE_PARENT_STATE_IS_SET")).isTrue();
    assertThat(isFlagSet(dest, "PFLAG_BACKGROUND_IS_SET")).isTrue();
    assertThat(isFlagSet(dest, "PFLAG_FOREGROUND_IS_SET")).isTrue();
    assertThat(isFlagSet(dest, "PFLAG_VISIBLE_HANDLER_IS_SET")).isTrue();
    assertThat(isFlagSet(dest, "PFLAG_FOCUSED_HANDLER_IS_SET")).isTrue();
    assertThat(isFlagSet(dest, "PFLAG_FULL_IMPRESSION_HANDLER_IS_SET")).isTrue();
    assertThat(isFlagSet(dest, "PFLAG_INVISIBLE_HANDLER_IS_SET")).isTrue();
    assertThat(isFlagSet(dest, "PFLAG_UNFOCUSED_HANDLER_IS_SET")).isTrue();
    assertThat(isFlagSet(dest, "PFLAG_VISIBLE_RECT_CHANGED_HANDLER_IS_SET")).isTrue();
  }

  @Test
  public void testPaddingIsSetFromDrawable() {
    YogaNode yogaNode = mock(YogaNode.class);
    InternalNode node =
        new DefaultInternalNode(new ComponentContext(getApplicationContext()), yogaNode);

    node.backgroundRes(background_with_padding);

    assertThat(isFlagSet(node, "PFLAG_PADDING_IS_SET")).isTrue();

    verify(yogaNode).setPadding(LEFT, 48);
    verify(yogaNode).setPadding(TOP, 0);
    verify(yogaNode).setPadding(RIGHT, 0);
    verify(yogaNode).setPadding(BOTTOM, 0);
  }

  @Test
  public void testPaddingIsNotSetFromDrawable() {
    InternalNode node = acquireInternalNode();

    node.backgroundRes(background_without_padding);

    assertThat(isFlagSet(node, "PFLAG_PADDING_IS_SET")).isFalse();
  }

  @Test
  public void testComponentCreateAndRetrieveCachedLayoutLS() {
    final ComponentContext baseContext = new ComponentContext(getApplicationContext());
    final ComponentContext c =
        ComponentContext.withComponentTree(baseContext, ComponentTree.create(baseContext).build());
    final LayoutState layoutState = new LayoutState(c);
    c.setLayoutStateContext(new LayoutStateContext(layoutState));

    final int unspecifiedSizeSpec = makeSizeSpec(0, UNSPECIFIED);
    final int exactSizeSpec = makeSizeSpec(50, EXACTLY);
    final Component textComponent = Text.create(c).textSizePx(16).text("test").build();
    final Size textSize = new Size();
    textComponent.measure(c, exactSizeSpec, unspecifiedSizeSpec, textSize);

    assertThat(layoutState.getCachedLayout(textComponent)).isNotNull();
    InternalNode cachedLayout = layoutState.getCachedLayout(textComponent);
    assertThat(cachedLayout).isNotNull();
    assertThat(cachedLayout.getLastWidthSpec()).isEqualTo(exactSizeSpec);
    assertThat(cachedLayout.getLastHeightSpec()).isEqualTo(unspecifiedSizeSpec);

    layoutState.clearCachedLayout(textComponent);
    assertThat(layoutState.getCachedLayout(textComponent)).isNull();
  }

  @Test
  public void testContextSpecificComponentAssertionPasses() {
    acquireInternalNode().assertContextSpecificStyleNotSet();
  }

  @Test
  public void testContextSpecificComponentAssertionFailFormatting() {
    final ComponentsLogger componentsLogger = mock(ComponentsLogger.class);
    final PerfEvent perfEvent = mock(PerfEvent.class);
    when(componentsLogger.newPerformanceEvent((ComponentContext) any(), anyInt()))
        .thenReturn(perfEvent);

    InternalNode node = acquireInternalNodeWithLogger(componentsLogger);
    node.alignSelf(YogaAlign.AUTO);
    node.flex(1f);

    node.assertContextSpecificStyleNotSet();
    assertThat(mComponentsReporter.getLoggedMessages())
        .contains(
            new Pair<>(
                ComponentsReporter.LogLevel.WARNING,
                "You should not set alignSelf, flex to a root layout in Column"));
  }

  @Test
  public void testDeepClone() {
    final ComponentContext context = new ComponentContext(getApplicationContext());
    context.setLayoutStateContextForTesting();

    InternalNode layout =
        createAndMeasureComponent(
            context,
            Column.create(context)
                .child(Row.create(context).child(Column.create(context)))
                .child(Column.create(context).child(Row.create(context)))
                .child(SolidColor.create(context).color(Color.RED))
                .build(),
            makeSizeSpec(0, UNSPECIFIED),
            makeSizeSpec(0, UNSPECIFIED));

    InternalNode cloned = layout.deepClone();

    assertThat(cloned).isNotNull();

    assertThat(cloned).isNotSameAs(layout);

    assertThat(cloned.getYogaNode()).isNotSameAs(layout.getYogaNode());

    assertThat(cloned.getChildCount()).isEqualTo(layout.getChildCount());

    assertThat(cloned.getChildAt(0).getTailComponent().getGlobalKey())
        .isEqualTo(layout.getChildAt(0).getTailComponent().getGlobalKey());
    assertThat(cloned.getChildAt(1).getTailComponent().getGlobalKey())
        .isEqualTo(layout.getChildAt(1).getTailComponent().getGlobalKey());
    assertThat(cloned.getChildAt(2).getTailComponent().getGlobalKey())
        .isEqualTo(layout.getChildAt(2).getTailComponent().getGlobalKey());

    assertThat(cloned.getChildAt(0).getYogaNode()).isNotSameAs(layout.getChildAt(0).getYogaNode());
    assertThat(cloned.getChildAt(1).getYogaNode()).isNotSameAs(layout.getChildAt(1).getYogaNode());
    assertThat(cloned.getChildAt(2).getYogaNode()).isNotSameAs(layout.getChildAt(2).getYogaNode());

    assertThat(cloned.getChildAt(0).getChildAt(0)).isNotSameAs(layout.getChildAt(0).getChildAt(0));
    assertThat(cloned.getChildAt(1).getChildAt(0)).isNotSameAs(layout.getChildAt(1).getChildAt(0));
  }

  private static boolean isFlagSet(InternalNode internalNode, String flagName) {
    long flagPosition = Whitebox.getInternalState(DefaultInternalNode.class, flagName);
    long flags = Whitebox.getInternalState(internalNode, "mPrivateFlags");

    return ((flags & flagPosition) != 0);
  }

  private static void clearFlag(InternalNode internalNode, String flagName) {
    long flagPosition = Whitebox.getInternalState(DefaultInternalNode.class, flagName);
    long flags = Whitebox.getInternalState(internalNode, "mPrivateFlags");
    flags &= ~flagPosition;
    Whitebox.setInternalState(internalNode, "mPrivateFlags", flags);
  }

  private static void assertEmptyFlags(InternalNode internalNode) {
    assertThat(((long) getInternalState(internalNode, "mPrivateFlags")) == 0).isTrue();
  }
}

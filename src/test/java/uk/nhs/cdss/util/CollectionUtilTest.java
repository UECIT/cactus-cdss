package uk.nhs.cdss.util;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Test;

public class CollectionUtilTest {

  private static class ItemA { }

  private static class ItemB { }

  private static class ItemA1 extends ItemA { }

  private static class ItemA2 extends ItemA { }

  @Test(expected = NullPointerException.class)
  public void filterAndCast_withNullList_shouldFail() {
    //noinspection ConstantConditions
    CollectionUtil.filterAndCast(null, Object.class);
  }

  @Test(expected = NullPointerException.class)
  public void filterAndCast_withNullType_shouldFail() {
    //noinspection ConstantConditions
    CollectionUtil.filterAndCast(emptyList(), null);
  }

  @Test
  public void filterAndCast_withEmptyList_shouldReturnEmpty() {
    var filteredList = CollectionUtil.filterAndCast(emptyList(), Object.class);
    assertThat(filteredList, Matchers.empty());
  }

  @Test
  public void filterAndCast_shouldFilterAndCastBasic() {
    var a = new ItemA();
    var b = new ItemB();
    var list = List.of(a, b);

    var filteredList = CollectionUtil.filterAndCast(list, ItemA.class);

    assertThat(filteredList, contains(a));
  }

  @Test
  public void filterAndCast_shouldFilterAndCastAncestor() {
    var a = new ItemA();
    var a1 = new ItemA1();
    var a2 = new ItemA2();
    var b = new ItemB();
    var list = List.of(a, a1, a2, b);

    var filteredList = CollectionUtil.filterAndCast(list, ItemA.class);

    assertThat(filteredList, contains(a, a1, a2));
  }

  @Test
  public void filterAndCast_shouldFilterAndCastDescendant() {
    var a = new ItemA();
    var a1 = new ItemA1();
    var a2 = new ItemA2();
    var b = new ItemB();
    var list = List.of(a, a1, a2, b);

    var filteredList = CollectionUtil.filterAndCast(list, ItemA1.class);

    assertThat(filteredList, contains(a1));
  }
}
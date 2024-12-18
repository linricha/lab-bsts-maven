package edu.grinnell.csc207.experiments;

import edu.grinnell.csc207.util.SimpleBST;

import java.io.PrintWriter;

/**
 * A set of simple experiments with SimpleBSTs.
 *
 * @author Your Name Here
 * @author Your Name Here
 * @author Samuel A. Rebelsky
 */
public class SimpleBSTExperiments {

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Add a bunch of simulated key/value pairs to a BST.
   *
   * @param example
   *   The tree we're adding to
   * @param strings
   *   The strings we're adding.
   */
  static void addStrings(SimpleBST<String, String> example, String[] strings) {
    for (String str : strings) {
      example.set(key(str), str);
    } // for
  } // add

  /**
   * Some experiments with getting values.
   *
   * @param pen
   *   The PrintWriter used to print output.
   * @param example
   *   The BST we're getting from.
   * @param strings
   *   The strings we're trying to get.
   */
  static void getExperiment(PrintWriter pen, SimpleBST<String, String> example,
      String[] strings) {
    for (String str : strings) {
      String key = key(str);
      pen.print(key + ": ");
      pen.flush();
      try {
        pen.println(example.get(key));
      } catch (Exception e) {
        pen.println(e);
      } // try/catch
    } // for
  } // getExperiment

  /**
   * Generate a key from a string.
   *
   * @param str
   *   The string
   *
   * @return
   *   A simple key.
   */
  static String key(String str) {
    return str.substring(0, 2);
  } // key(String)

  /**
   * An experiment with removing elements.
   *
   * @param pen
   *   The PrintWriter used to print output.
   * @param example
   *   The BST we're removing elements from.
   * @param str
   *   The string we're removing.
   */
  static void removeExperiment(PrintWriter pen,
      SimpleBST<String, String> example, String str) {
    pen.println("Removing '" + str + "'");
    example.remove(key(str));
    example.dump(pen);
    pen.println();
  } // removeExperiment

  // +------+--------------------------------------------------------
  // | Main |
  // +------+

  /**
   * Run the experiments.
   *
   * @param args
   *   Command-line arguments (ignored).
   */
  public static void main(String[] args) {
    PrintWriter pen = new PrintWriter(System.out, true);

    SimpleBST<String, String> example = new SimpleBST<String, String>();

    String[] phase1 = {"civet", "aardvark", "koala", "llama", "moose",
        "jackalope", "baboon", "dingo"};
    String[] phase2 = {"vicuna", "mouse", "aardwolf", "dog", "emu"};

    pen.println("Starting phase 1");
    addStrings(example, phase1);
    example.dump(pen);
    pen.println();

    pen.println("Getting values");
    getExperiment(pen, example, phase1);
    example.dump(pen); // Does the tree change?
    pen.println();

    pen.println("Checking forEach");
    example.forEach((key, value) -> pen.println(key + ": " + value));

    pen.println("Starting phase 2");
    addStrings(example, phase2);
    example.dump(pen);
    pen.println("HI");

    removeExperiment(pen, example, "vicuna"); // leaf?
    removeExperiment(pen, example, "koala"); // middle?
    removeExperiment(pen, example, "civet"); // root?
  } // main(String[])

} // class SimpleBSTExperiments

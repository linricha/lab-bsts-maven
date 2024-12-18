package edu.grinnell.csc207.util;

import java.io.PrintWriter;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Stack;
import java.util.function.BiConsumer;

/**
 * A simple implementation of binary search trees.
 *
 * @author Your Name Here
 * @author Your Name Here
 * @author Samuel A. Rebelsky
 *
 * @param <K>
 *   The type used for keys.
 * @param <V>
 *   The type used for values.
 */
public class SimpleBST<K, V> implements SimpleMap<K, V> {

  // +--------+------------------------------------------------------
  // | Fields |
  // +--------+

  /**
   * The root of our tree. Initialized to null for an empty tree.
   */
  BSTNode<K, V> root;

  /**
   * The comparator used to determine the ordering in the tree.
   */
  Comparator<? super K> order;

  /**
   * The size of the tree.
   */
  int size;

  /**
   * A cached value (useful in some circumstances.
   */
  V cachedValue;

  // +--------------+------------------------------------------------
  // | Constructors |
  // +--------------+

  /**
   * Create a new binary search tree that orders values using the
   * specified comparator.
   *
   * @param comp
   *   The comparator used to compare keys.
   */
  public SimpleBST(Comparator<? super K> comp) {
    this.order = comp;
    this.root = null;
    this.size = 0;
    this.cachedValue = null;
  } // SimpleBST(Comparator<K>)

  /**
   * Create a new binary search tree that orders values using a
   * not-very-clever default comparator.
   */
  public SimpleBST() {
    this((k1, k2) -> k1.toString().compareTo(k2.toString()));
  } // SimpleBST()

  // +-------------------+-------------------------------------------
  // | SimpleMap methods |
  // +-------------------+

  /**
   * Set the value associated with key.
   *
   * @param key
   *   The key to use.
   * @param value
   *   The associated value.
   *
   * @return the previous value associated with key (or null, if there's no
   *         such value)
   *
   * @throws NullPointerException if the key is null.
   */
  @Override
  public V set(K key, V value) {
    if (key == null) {
      throw new NullPointerException();
    }

    BSTNode<K,V> prevNode = this.root;
    BSTNode<K, V> current = this.root;
    V prevVal = null;
    while (current != null && prevVal == null) {
      if (order.compare(key, current.key) > 0) { // right
        prevNode = current;
        current = current.right;
      } else if (order.compare(key, current.key) < 0){ // left
        prevNode = current;
        current = current.left;
      } else { // = 0 i.e. found key
        prevVal = current.value;
        current.value = value;
      }
    }
    // i.e. traversed through tree correctly and not found
    if (current == null) {
      if (prevNode == current) { // root node is null
        this.root = new BSTNode<K,V>(key, value);
      } else if (order.compare(key, prevNode.key) > 0) { // right
        prevNode.right = new BSTNode<K, V>(key, value);
      } else { // left 
        // no equals since current would not be null then.
        prevNode.left = new BSTNode<K, V>(key, value);
      }
      this.size++;
    }
    return prevVal;
  } // set(K, V)

  /**
   * Get the value associated with key.
   *
   * @param key
   *   The key to use.
   *
   * @return the corresponding value.
   *
   * @throws IndexOutOfBoundsException if the key is not in the map.
   * @throws NullPointerException if the key is null.
   */
  @Override
  public V get(K key) {
    if (key == null) {
      throw new NullPointerException("null key");
    } // if
    return get(key, root);
  } // get(K, V)

  /**
   * Determine how many key/value pairs are in the map.
   *
   * @return the number of key/value pairs.
   */
  @Override
  public int size() {
    return this.size; 
  } // size()

  /**
   * Determine if a key appears in the table.
   *
   * @param key
   *   The key to check.
   *
   * @return true if the key appears in the table and false otherwise.
   */
  @Override
  public boolean containsKey(K key) {
    return false;       // STUB
  } // containsKey(K)

  /**
   * Remove the value with the given key. Also remove the key.
   *
   * @param key
   *   The key to remove.
   *
   * @return The associated value (or null, if there is no associated value).
   * @throws NullPointerException if the key is null.
   */
  @Override
  public V remove(K key) {
    if (key == null) {
      throw new NullPointerException();
    }

    V removedVal = null;

    BSTNode<K,V> prevNode = this.root;
    BSTNode<K, V> current = this.root;
    boolean isCurrentRightNode = false;

    // find node with key
    // Stops when found or when there is none
    while (removedVal == null && current != null) {
      if (order.compare(key, current.key) > 0) { // right
        prevNode = current;
        current = current.right;
        isCurrentRightNode = true;
      } else if (order.compare(key, current.key) < 0){ // left
        prevNode = current;
        current = current.left;
        isCurrentRightNode = false;
      } else { // = 0 i.e. found key
        removedVal = current.value;
      }
    }

    if (current != null) { // there is a node to be removed

      boolean isRoot = order.compare(prevNode.key, current.key) == 0;


      if (current.right == null && current.left == null) { // leaf
        if (isRoot) {
          this.root = null;
        } else {
          if (isCurrentRightNode) { // current is right
            prevNode.right = null;
          } else { // current is left
            prevNode.left = null;
          }
        }
      } else if (current.right != null && current.left != null) { // two children
        reUpdateTreeTwoChildren(current, prevNode, isCurrentRightNode, isRoot);
      } else if (current.right != null) { // left is null
        if (isRoot) {
          this.root = current.right;
        } else {
          if (isCurrentRightNode) { // current is right
            prevNode.right = current.right;
          } else {
            prevNode.left = current.right;
          }
        }
      } else { // right is null
        if (isRoot) {
          this.root = current.left;
        } else {
          if (isCurrentRightNode) { // current is right
            prevNode.right = current.left;
          } else {
            prevNode.left = current.left;
          }
        }
      }
    }
    
    if (removedVal != null) {
      this.size--;
    }

    return removedVal;
  } // remove(K)

  private void reUpdateTreeTwoChildren(BSTNode<K, V> current, BSTNode<K, V> prevNode, boolean isCurrentRightNode, boolean isRoot) {
    BSTNode<K, V> removedNode = current;
    if (order.compare(current.left.key, current.right.key) > 0) { // left is greater

        // reupdate connections
        if (isRoot) {
          this.root = current.left;
        } else {
          if (isCurrentRightNode) {
            prevNode.right = current.left;
          } else {
            prevNode.left = current.left;
          }
        }

        current = current.left;
        while (current.right != null) { // go to rightmost side of current and attach right subtree
          current = current.right;
        }
        current.right = removedNode.right;
      } else { // right is greater

        // reupdate connections
        if (isRoot) {
          this.root = current.right;
        } else {
          if (isCurrentRightNode) {
            prevNode.right = current.right;
          } else {
            prevNode.left = current.right;
          }
        }

        current = current.right;
        while (current.left != null) { // go to leftmost side of current and attach left subtree
          current = current.left;
        }
        current.left = removedNode.left;
      }
    }
  

  /**
   * Get an iterator for all of the keys in the map.
   *
   * @return an iterator for all the keys.
   */
  @Override
  public Iterator<K> keys() {
    return new Iterator<K>() {
      Iterator<BSTNode<K, V>> nit = SimpleBST.this.nodes();

      @Override
      public boolean hasNext() {
        return nit.hasNext();
      } // hasNext()

      @Override
      public K next() {
        return nit.next().key;
      } // next()

      @Override
      public void remove() {
        nit.remove();
      } // remove()
    };
  } // keys()

  /**
   * Get an iterator for all of the values in the map.
   *
   * @return an iterator for all the values.
   */
  @Override
  public Iterator<V> values() {
    return new Iterator<V>() {
      Iterator<BSTNode<K, V>> nit = SimpleBST.this.nodes();

      @Override
      public boolean hasNext() {
        return nit.hasNext();
      } // hasNext()

      @Override
      public V next() {
        return nit.next().value;
      } // next()

      @Override
      public void remove() {
        nit.remove();
      } // remove()
    };
  } // values()

  /**
   * Apply a procedure to each key/value pair.
   *
   * @param action
   *   The action to perform for each key/value pair.
   */
  @Override
  public void forEach(BiConsumer<? super K, ? super V> action) {
    forEachHelper(action, this.root);
  } // forEach

  private void forEachHelper(BiConsumer<? super K, ? super V> action, BSTNode<K, V> node) {
    if (node != null) {
      action.accept(node.key,node.value);
      forEachHelper(action, node.left);
      forEachHelper(action, node.right);
    } // if
  } // forEachHelper

  // +----------------------+----------------------------------------
  // | Other public methods |
  // +----------------------+

  /**
   * Dump the tree to some output location.
   *
   * @param pen
   *   The PrintWriter used to dump the tree.
   */
  public void dump(PrintWriter pen) {
    dump(pen, root, "");
  } // dump(PrintWriter)

  // +---------+-----------------------------------------------------
  // | Helpers |
  // +---------+

  /**
   * Dump a portion of the tree to some output location.
   *
   * @param pen
   *   The PrintWriter used to dump the subtree.
   * @param node
   *   The root of the subtree.
   * @param indent
   *   How much to indent the subtree.
   */
  void dump(PrintWriter pen, BSTNode<K, V> node, String indent) {
    if (node == null) {
      pen.println(indent + "<>");
    } else {
      pen.println(indent + node.key + ": " + node.value);
      if ((node.left != null) || (node.right != null)) {
        dump(pen, node.left, indent + "  ");
        dump(pen, node.right, indent + "  ");
      } // if has children
    } // else
  } // dump

  /**
   * Get the value associated with a key in a subtree rooted at node.  See the
   * top-level get for more details.
   *
   * @param key
   *   The key to search for.
   * @param node
   *   The root of the subtree to look through.
   *
   * @return
   *   The corresponding value.
   *
   * @throws IndexOutOfBoundsException
   *   when the key is not in the subtree.
   */
  V get(K key, BSTNode<K, V> node) {
    if (node == null) {
      throw new IndexOutOfBoundsException("Invalid key: " + key);
    } // if
    int comp = order.compare(key, node.key);
    if (comp == 0) {
      return node.value;
    } else if (comp < 0) {
      return get(key, node.left);
    } else {
      return get(key, node.right);
    } // if/else
  } // get(K, BSTNode<K, V>)

  /**
   * Get an iterator for all of the nodes. (Useful for implementing the
   * other iterators.)
   *
   * @return an iterator for all of the other nodes.
   */
  Iterator<BSTNode<K, V>> nodes() {
    return new Iterator<BSTNode<K, V>>() {

      Stack<BSTNode<K, V>> stack = new Stack<BSTNode<K, V>>();
      boolean initialized = false;
      boolean stackMade = false;

      @Override
      public boolean hasNext() {
        checkInit();
        return !stack.empty();
      } // hasNext()

      @Override
      public BSTNode<K, V> next() {
        checkInit();
        BSTNode<K,V> current;

        if (!stackMade) {
          stackSetUp(stack.peek()); // setup with root
          stackMade = true;
        }

        if (hasNext()) {
          return stack.pop();
        } // if
        return null;
      } // next();

      private void stackSetUp(BSTNode<K, V> node) {
        if (node != null) {
          if (node.right != null) {
            stack.push(node.right);
            stackSetUp(node.right);
          }
          
          if (node.left != null) {
            stack.push(node.left);
            stackSetUp(node.left);
          }
        }
      }

      void checkInit() {
        if (!initialized) {
          stack.push(SimpleBST.this.root);
          initialized = true;
        } // if
      } // checkInit
    }; // new Iterator
  } // nodes()

} // class SimpleBST

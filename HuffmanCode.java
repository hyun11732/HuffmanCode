
import java.util.*;
import java.io.*;

public class HuffmanCode {
   private HuffmanNode root;
   
   private static class HuffmanNode implements Comparable<HuffmanNode> {
      public final int[] data;
      public HuffmanNode left;
      public HuffmanNode right;
      
      // This constructor takes int[] "data", HuffmanNode "left" and "right" as parameters. 
      // It initializes HuffmanNode which has "data" itself and has two connections
      // which connects this HuffmanNode to "left" and "right" HuffmanNode.
      public HuffmanNode(int[] data, HuffmanNode left, HuffmanNode right) {
         this.data = data;
         this.left = left;
         this.right = right;
      }
      
      // It takes int[] "data" as a parameter and initializes a single HuffmanNode
      // which has "data" itself and no connections to left and right.
      public HuffmanNode(int[] data) {
         this(data, null, null);
      }
            
      // This takes HuffmanNode "node" as parameter and return integer based on
      // difference between "node" and this HuffmanNode. If this HuffmanNode's data
      // array's first position(not zero position, data[1]) is bigger than node data
      // array's first position, it will return positive number. If they are same, it 
      // will return 0. If this HuffmanNode's is smaller than "node"'s, it will return
      // negative number. 
      @Override
      public int compareTo(HuffmanNode node) {
         return Integer.compare(this.data[1], node.data[1]);
         
      }
   }
   
   // This takes int[] "frequencies" as a parameter. It first changes "frequencies" into
   // HuffmanNode and initializes a compressed version of HuffmanCode binary tree.
   // Character which has less frequency will go left and larger frequency will 
   // go right on the HuffmanNode tree. This constructor is  a compressed version. 
   public HuffmanCode(int[] frequencies) {
      PriorityQueue<HuffmanNode> queue = new PriorityQueue<HuffmanNode>();
      int character = 0;
      for(int count : frequencies) {
         if(count > 0) {
            int[] store = {character, count};
            queue.add(new HuffmanNode(store));
         }
         character++;
      }
      this.root = makeTreeC(queue).peek(); 
   }
   
   // This takes PriorityQueue<HuffmanNode> "queue" as a parameter and actually
   // initializes a HuffmanNode object, a binary tree, from "queue". It will pick
   // two the least frequencies and make a tree by making less frequency go left 
   // larger frequency go right. It will keep repeat this until it makes a final 
   // HuffmanNode tree. Then, it returns PriorityQueue<HuffmanNode> which has only
   // one thing, a final HuffmanNode tree(compressed version). 
   private PriorityQueue<HuffmanNode> makeTreeC(PriorityQueue<HuffmanNode> queue) {
      if(queue.size()  == 1) {
         return queue;
      } 
      HuffmanNode current1 = queue.remove();
      HuffmanNode current2 = queue.remove();
      int[] frequency = {-1, current2.data[1] + current1.data[1]};
      HuffmanNode tree = new HuffmanNode(frequency);
      tree.left = current1;
      tree.right =  current2;         
      queue.add(tree);
      queue = this.makeTreeC(queue); 
      return queue;
   }
   
   // This takes Scanner "input" as a parameter and initializes a HuffmanCode
   // object based on "input". This initializes decompressed version of HuffmanCode 
   // tree. 
   public HuffmanCode(Scanner input) {
      while(input.hasNextLine()) {
         int n = Integer.parseInt(input.nextLine());
         String code = input.nextLine();
         int[] nodeData = {n, 0};
         this.root = this.makeTreeD(code, new HuffmanNode(nodeData), this.root);
      }
   }
   
   // This takes String "code", HuffmanNode "insert" and "current" as parameters 
   // and initializes a decompressed version of HuffmanNode binary tree. We consider
   // left is '0' and right is '1' and thus each number of "code" indicates the direction
   // When "current" arrives where "code" indicated, it will insert "insert" HuffmanNode.
   // Then it will return the HuffmanNode including "insert".
   private HuffmanNode makeTreeD(String code, HuffmanNode insert, HuffmanNode current) {
      if(code.isEmpty()) {
         current = insert;
         return current;
      } 
      if (current == null) {
         int[] empty = {-1, -1};
         current = new HuffmanNode(empty);
      } 
      char map = code.charAt(0);
      if(map == '0') {
         current.left = this.makeTreeD(code.substring(1), insert, current.left);
      } else {
         current.right = this.makeTreeD(code.substring(1), insert, current.right);
      }
      return current;
   }
   
   // This method takes PrintStream "output" as a parameter and save HuffmanCode on
   // new file which ends with ".code". The first line indicates a charater's number and
   // next line indicates charater's position on the tree. '0' means left and
   // '1' means right. 
   public void save(PrintStream output) {
      this.save(output, this.root, "");
   }
   
   // This takes PrintStream "output", HffumanNode "current", and String "num" as 
   // parameters and actually write Huffman code on the file which name ends with 
   // ".code". The first line will tell a character's number(like 'a' is  97) and
   // next line indicates charcter's position on the tree. '0' means left '1'
   // means right. 
   private void save(PrintStream output, HuffmanNode current, String num) {
      if(current.left == null && current.right == null) {
         output.println(current.data[0]);
         output.println(num);
      } else {
         num += "0";
         this.save(output, current.left, num);
         num = num.substring(0, num.length() - 1);
         num += "1";
         this.save(output, current.right, num);
      }
   }
   
   // This method takes BitInputStream "input" and PrintStream "output" as 
   // parameters. This method is used when a user decompreses a file. It changes
   // "input" which is written in binary number and translate it into
   // actual character than write it at "output", a new file, ends with
   // ".new"
   public void translate(BitInputStream input, PrintStream output) {
      while(input.hasNextBit()) {
         input = this.translate(input, output, this.root);
      }
   }
   
   // This takes BitInputStream "input", printStream "output", and HuffmanNode 
   // "current" as parameters and translate binary "input" to characters by
   // reading "current" HuffmanNode. Then, it prints characters on "output"
   // file. 
   private BitInputStream translate(BitInputStream input, PrintStream output,
         HuffmanNode current) {
      if(current.left == null & current.right == null) {
         output.write(current.data[0]);
         return input;
      } 
      int number = input.nextBit();
      if(number == 0) {
         input = this.translate(input, output, current.left);
      } else {
         input = this.translate(input, output, current.right);
      }
      return input;
   }  
}

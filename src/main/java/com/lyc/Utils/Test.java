package com.lyc.Utils;

/**
 * CREATE WITH Lenovo
 * DATE 2019/5/6
 * TIME 19:43
 */
class Test {

    String name;

    public Test(String name) {
        this.name = name;
    }

    public static int num2(String express, boolean desired) {
        if (express == null || express.equals("")) {
            return 0;
        }
        char[] exp = express.toCharArray();
        int[][] t = new int[exp.length][exp.length];
        int[][] f = new int[exp.length][exp.length];
        t[0][0] = exp[0] == '0' ? 0 : 1;
        f[0][0] = exp[0] == '1' ? 0  : 1;
        for (int i = 2; i < exp.length; i += 2) {
            t[i][i] = exp[i] == '0' ? 0 : 1;
            f[i][i] = exp[i] == '1' ? 0 : 1;
            for(int j = i - 2;j>=0;j-=2) {
                for(int k  = j ; k < i ;k+= 2) {
                    if (exp[k + 1] == '&') {
                        t[j][i] += t[j][k] * t[k + 2][i];
                        f[j][i] += (f[j][k] + t[j][k]) * f[k + 2][i] + f[j][k] * t[k + 2][i];
                    } else if (exp[k + 1] == '|') {
                        t[j][i] += (f[j][k] + t[j][k]) * t[k + 2][i] + t[j][k] * f[k + 2][i];
                        f[j][i] += f[j][k] * f[k + 2][i];
                    } else {
                        t[j][i] += f[j][k] * t[k + 2][i] + t[j][k] * f[k + 2][i];
                        f[j][i] += f[j][k] * f[k + 2][i] + t[j][k] * t[k + 2][i];
                    }

                }
            }
        }
        return desired ? t[0][t.length - 1] : f[0][f.length - 1];
    }

    class TreeNode{
        int val;
        TreeNode left;
        TreeNode right;

        TreeNode(int t) {
            this.val = t;
        }
    }

    private int ans = 0;
    public int rangeSumBST(TreeNode root, int L, int R) {

        if (root != null) {
            if (root.val >= L && root.val <= R) {
                ans += root.val;
            }
            if (root.val > L) {
                rangeSumBST(root.left, L, R);
            }
            if (root.val < R) {
                rangeSumBST(root.right, L, R);
            }
        }

        return ans;
    }

    public static String removeOuterParentheses(String S) {
        char[] kagos = S.toCharArray();
        int countOfleft = 0;
        StringBuilder sb = new StringBuilder();
        for(int i = 0 ; i < kagos.length ;i++) {
            if (kagos[i] == '(') {
                if (countOfleft++ != 0) {
                    sb.append( '(' );
                }
            } else {
                if (--countOfleft != 0) {
                    sb.append(')');
                }
            }
        }
        return sb.toString();
    }

    public TreeNode bstFromPreorder(int[] preorder) {
        return create(0,preorder.length,preorder);
    }

    public TreeNode create(int start, int end, int[] preorder) {
        if (start > end) {
            return null;
        }
        TreeNode root = new TreeNode(preorder[start]);
        int i = start;
        while (i < end) {
            if (preorder[++i] > preorder[start]) {
                break;
            }
        }
        root.left = create(start, i, preorder);
        root.right = create(i + 1, end, preorder);
        return root;
    }

    public static void main(String[] args) {
        System.out.println(removeOuterParentheses("(()())(())(()(()))"));
    }


}

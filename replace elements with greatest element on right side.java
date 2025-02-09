class Solution {
    public int[] replaceElements(int[] arr) {
        int maxi = -1;
        int n = arr.length;
        for(int i = n - 1; i >= 0; i--){
            int x = arr[i];
            arr[i] = maxi;
            maxi = Math.max(maxi, x);
        }
        return arr;
    }
}

import java.util.*;

// 1. Divide and Conquer
// 2. Does not use a temporary array 
// 3. Better than Merge Sort

// Rules :
// 1. Pick a Pivot
// 2. Pivot can be any random element
// 3. Pick the pivot and place it in its correct place
// after a point there will be all "Smaller than pivot"
class QuickSort {
    public int[] quickSort(int[] nums, int low, int high) {

        if(low<high){
            int partition = partition(nums, low, high);
            quickSort(nums,low,partition-1);
            quickSort(nums,partition+1,high);
        }
        return nums;
    }

    public int partition(int[] nums, int low, int high){
        // System.out.print("Subarray: ");
        // for (int k = low; k <= high; k++) {
        //     System.out.print(nums[k] + " ");
        // }
        // System.out.println();

        Random rand = new Random();
        int randIndex = rand.nextInt(high - low + 1) + low;

        int temps = nums[low];
        nums[low] = nums[randIndex];
        nums[randIndex] = temps;

        int pivot = nums[low];
        System.out.println("Pivot selected: " + pivot);  // Print the selected pivot
        int i = low;
        int j = high;

        while(i < j){
            //find greater than pivot on left side
            while( i <= high-1 && nums[i] <= pivot){
                i++;
                //we are going from left to right so we are doing high-1 
            }
            //find smaller than pivot
            while(j >= low+1 && nums[j] >= pivot){
                j--;
            }
            //swap i and j if still i is less than j
            if(i<j){
                int temp = nums[i];
                nums[i]=nums[j];
                nums[j]=temp;
            }
            
            //now after i and j have crossed each other its time to bring pivot to its correct place
            // so swap arr[low] that is our pivot with arr[j] 


            // so now after swapping our pivot becomes j which is our partition index 
            // and now we can perform the qs by recursion
            // because we have smaller elements on left and greater on right
            // we have to repeat the same for them as above but with different range
        }
            int temp = nums[low];
            nums[low]=nums[j];
            nums[j]=temp;

            return j;
    }

    public static void main(String[] args){
        int[] nums = {4,1,3,5,6};
        int n = nums.length;

        System.out.println("Before Merge Sorting");
        for(int i=0; i<n;i++){
            System.out.print(nums[i] + " ");
        }

        System.out.println();

        QuickSort quickSort = new QuickSort();

        int[] sortednums = quickSort.quickSort(nums, 0, n-1);

        System.out.println("After Merge Sorting");

        for(int i=0; i<n;i++){
            System.out.print(sortednums[i] + " ");
        }

        System.out.println();

    }
}
import java.util.*;



class QuickSort {
    public int[] quickSort(int[] nums, int low, int high) {
        if (low < high) {
            int partitionIndex = partition(nums, low, high);
            quickSort(nums, low, partitionIndex);
            quickSort(nums, partitionIndex + 1, high);
        }
        return nums;
    }

// here we are using Hoare Partition Method 

    public int partition(int[] nums, int low, int high) {
        // Randomly select a pivot and swap it with the element at 'low'
        Random rand = new Random();
        int randIndex = rand.nextInt(high - low + 1) + low;
        int temp = nums[low];
        nums[low] = nums[randIndex];
        nums[randIndex] = temp;

        int pivot = nums[low];
        System.out.println("Pivot selected: " + pivot);

        int i = low - 1;  // Start i just before the low index
        int j = high + 1; // Start j just after the high index

        while (true) {
            // Move i to the right until nums[i] >= pivot
            do {
                i++;
            } while (nums[i] < pivot); // increment till all the elements we are encountering are smaller than the pivot, if we get greater than or equal to pivot then stop

            // Move j to the left until nums[j] <= pivot
            do {
                j--;
            } while (nums[j] > pivot);  // increment till all the elements we are encountering are greater than the pivot, if we get smaller than or equal to pivot then stop

            if (i >= j) {
                return j;  // Return j as the partition index
            }

            // Swap elements at i and j
            temp = nums[i];
            nums[i] = nums[j];
            nums[j] = temp;
        }
    }

    public static void main(String[] args) {
        int[] nums = {4, 1, 3, 5, 6};
        int n = nums.length;

        System.out.println("Before Quick Sorting");
        for (int i = 0; i < n; i++) {
            System.out.print(nums[i] + " ");
        }
        System.out.println();

        QuickSort quickSort = new QuickSort();
        int[] sortednums = quickSort.quickSort(nums, 0, n - 1);

        System.out.println("After Quick Sorting");
        for (int i = 0; i < n; i++) {
            System.out.print(sortednums[i] + " ");
        }
        System.out.println();
    }
}

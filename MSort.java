import java.util.*;

class MergeSort {

    public int[] mergeSort(int[] nums, int low, int high){
        if(low>=high){
            return nums;
        }

        int mid = (low+high)/2;

        mergeSort(nums, low, mid);
        mergeSort(nums, mid+1, high);

        merge(nums, low, mid, high);
        return nums;
    }

    public static void merge(int[] nums, int low, int mid, int high){
        List<Integer> temp = new ArrayList<>();

        int left = low;
        int right = mid+1;
        while(left <= mid && right <= high){
            if(nums[left] < nums[right]){
                temp.add(nums[left]);
                left++;
            }else{
                temp.add(nums[right]);
                right++;
            }
        }
        while(left<=mid){
            temp.add(nums[left]);
            left++;
        }
        while(right<=high){
            temp.add(nums[right]);
            right++;
        }

        for(int i=low;i<=high;i++){
            nums[i]=temp.get(i-low);
        }
    }
    
    public static void main(String[] args){
        int[] nums = {3,1,2,4,1,5,6,2,4};
        int n = nums.length;

        System.out.println("Before Merge Sorting");
        for(int i=0; i<n;i++){
            System.out.print(nums[i] + " ");
        }

        System.out.println();

        MergeSort MSort = new MergeSort();

        int[] sortednums = MSort.mergeSort(nums, 0, n-1);

        System.out.println("After Merge Sorting");

        for(int i=0; i<n;i++){
            System.out.print(sortednums[i] + " ");
        }

        System.out.println();

    }
}
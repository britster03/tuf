class Solution {
    public int GCD(int n1, int n2) {
        // gcd is a number which divides both of them
        // 1 will always be the divisor which will divide both of them
        // int largest = 1;
        // for(int i = 2; i < Math.min(n1,n2); i++){
        //     if( n1 % i == 0 && n2 % i == 0){
        //         largest = i;
        //     }
        // }
        // return largest;

        // End of Brute Force Approach O(min(n1,n2))


        // int largest = 1;
        // for(int i = Math.min(n1,n2); i >= 1 ; i--){
        //     if( n1 % i == 0 && n2 % i == 0){
        //         largest = i;
        //         break;
        //     }
        // }
        // return largest;

        // End of better approach

        // Euclidean Method states that
        // gcd(n1, n2) = gcd(n1 - n2, n2) given that n1 > n2
        // why is this so?
        // they are stating that whatever is the difference 
        // (n2-n1) it is always divisible by the gcd 
        // (n2 - n1) % gcd == 0 
        // subtract the smaller number from the greater number
        // n1 = 35, n2 = 10
        // -10
        // s2 :- n1 = 25, n2 = 10
        // -10
        // s3 :- n1 = 15, n2 = 10
        // -10
        // s4 :- n1 = 5, n2 = 10
        // now here the smallest number is 5 so reduce by -5
        // s5 :- n1 = 5, n2 = 5
        // -5 
        // s6 :- n1 = 0, n2 = 5
        // you can pickup any of them as largest
        // the moment you reach 0 for any of the number, the other number will always be your gcd


        while( n1 != 0 && n2 != 0){
            if(n2 > n1){
                n2 = n2 - n1;
            } else {
                n1 = n1 - n2;
            }
        }
        if(n2 == 0){
            return n1;
        }else{
            return n2;
        }
        
        // end of optimal approach that is the Euclidean Method of finding GCD
    }
}
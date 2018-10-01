{-
William Johnke
ID: 14253530
October 15, 2017
CS 4450 - Principles of Programming Languages
-}


module Homework2 where
import Test.QuickCheck
-- Function prob1
-- @type  prob1 :: (a->b) -> (a->Bool) -> [a] -> [b]
-- @param 
-- @output 
-- @description:
{-
This function is a simple rewrite of the "listComp" function,
it takes a list, filters it by applying a predicate "p" to it,
and maps the given function back on to the filtered list
-}
-- listComp f p xs = [ f x | x <- xs, p x]
prob1 :: (a->b) -> (a->Bool) -> [a] -> [b]
prob1 _ _ [] = []
prob1 f p xs =map f (filter p xs)

-- Function prob2
-- @type   prob2 :: Integer -> [Integer]
-- @param  
-- @output
-- @description:
{-
This function takes an integer and returns back that integer's digits
split into a list. If the number is negative it returns an empty list.
It will recursively call the number, each time truncating its least decimal
place and appending it to the list.
-}
prob2 :: Integer -> [Integer]
prob2 num | num<0 = []
		  | num <10 = [num]   -- Final digit/base case
		  | otherwise = prob2 (num `div` 10) ++ [(num `mod` 10)] --Recursive step

-- Function prob3
-- @type   prob3 :: Integer -> [Integer]
-- @param  
-- @output
-- @description:
{-
This function is simply the reverse of the function in prob2, it is just appending
the value retrieved from the recursive calls after the digit is truncated
-}
prob3 :: Integer -> [Integer]
prob3 num | num<0 = []
		  | num <10 = [num]   -- Final digit/base case
		  | otherwise = [(num `mod` 10)] ++ prob2 (num `div` 10) --Recursive step

-- Function prob4
-- @type   prob4 :: [Integer] -> [Integer]
-- @param  
-- @output
-- @description:
{-
This function doubles every other element in a given list and returns it back
It only works on lists of even lengths, unfortunately.
-}
prob4 :: [Integer] -> [Integer]
prob4 [] =[]
prob4 (x:[]) = [x]                            --Last element
prob4 (x:(xs:[])) = [x*2] ++ prob4 [xs]       --Second to last element
prob4 (x:(y:xs)) = [x*2] ++ [y] ++ prob4 (xs) --Else

-- Function prob5
-- @type  prob5 :: [Integer] -> Integer 
-- @param  
-- @output
-- @description:
{-
This function calculates the sum of a list of integers, utilizing the function
implemented in prob2 to split each element into its digits, and then a helper 
"findSum" function to calculate the sum of all those digits 
-}
prob5 :: [Integer] -> Integer
prob5 [] = 0
prob5 (x:xs)| x<0 = 0
			|otherwise = findSum (prob2 x) + prob5 xs

findSum :: [Integer] -> Integer
findSum [] =0
findSum (x:xs) | x<0 =0
		 | otherwise = x + findSum xs
        
---------------------------------------------              
--               Unit Tests                --
---------------------------------------------  
test_prob1 :: IO ()
test_prob1  = do
  putStrLn "Problem 1 Results:"
  prob1_test1
  prob1_test2
test_prob2 :: IO ()
test_prob2  = do
  putStrLn "Problem 2 Results:"
  prob2_test1
  prob2_test2
test_prob3 :: IO ()
test_prob3  = do
  putStrLn "Problem 3 Results:"
  prob3_test1
test_prob4 :: IO ()
test_prob4  = do
  putStrLn "Problem 4 Results:"
  prob4_test1
  prob4_test2
test_prob5 :: IO ()
test_prob5  = do
  putStrLn "Problem 5 Results:"
  prob5_test1
test_probs :: IO ()
test_probs  = do
  putStrLn "-------- All Problem Results --------"
  test_prob1
  test_prob2
  test_prob3
  test_prob4
  test_prob5
  putStrLn "-------------------------------------"
prob1_test1 = quickCheckWith (stdArgs {maxSuccess = 1000}) prob1_property1
  where
    prob1_property1 :: [Integer] -> Bool
    prob1_property1 xs = lComp (+1) (even) xs == prob1 (+1) (even) xs
      where lComp f p xs = [ f x | x <- xs, p x]
prob1_test2 = quickCheckWith (stdArgs {maxSuccess = 1000}) prob1_property2
  where
    prob1_property2 :: [Int] -> Bool
    prob1_property2 xs = lComp (*2) (odd) xs == prob1 (*2) (odd) xs
      where lComp f p xs = [ f x | x <- xs, p x]
prob2_test1 = quickCheckWith (stdArgs {maxSuccess = 1000}) prob2_property1
  where
    prob2_property1 :: Integer -> Bool
    prob2_property1 xs = abs xs == (go1 . prob2) (abs xs)
      where go1 :: [Integer] -> Integer
            go1 xs
              | (null xs) = 0
              | otherwise = let pos = filter (> -1) xs
                            in  read (foldl (++) "" $ map show pos) :: Integer
prob2_test2 = quickCheckWith (stdArgs {maxSuccess = 1000}) prob2_property2
  where
    prob2_property2 :: Integer -> Bool
    prob2_property2 xs  = prob2 xs == prob2_dual xs
      where prob2_dual :: Integer -> [Integer]
            prob2_dual x
              | x < 0      = []
              | otherwise  = map go' $ show x
              where go' '0' = 0
                    go' '1' = 1
                    go' '2' = 2
                    go' '3' = 3
                    go' '4' = 4
                    go' '5' = 5
                    go' '6' = 6
                    go' '7' = 7
                    go' '8' = 8
                    go' '9' = 9
prob3_test1 = quickCheckWith (stdArgs {maxSuccess = 1000}) prob3_property1
  where
    prob3_property1 :: Integer -> Bool
    prob3_property1 xs  = prob3 xs == go1 xs
      where go1 :: Integer -> [Integer]
            go1 n | n < 0      = []
                  | otherwise  = reverse $ map go' $ show n
              where go' '0' = 0
                    go' '1' = 1
                    go' '2' = 2
                    go' '3' = 3
                    go' '4' = 4
                    go' '5' = 5
                    go' '6' = 6
                    go' '7' = 7
                    go' '8' = 8
                    go' '9' = 9
prob4_test1 = quickCheckWith (stdArgs {maxSuccess = 1000}) prob4_property1
  where
    prob4_property1 :: Integer -> Integer -> Integer -> Integer -> Bool
    prob4_property1 w x y z = [(w + w),x, (y + y), z] == prob4 [w,x,y,z]
prob4_test2 = quickCheckWith (stdArgs {maxSuccess = 1000}) prob4_property2
  where
    prob4_property2 :: Integer -> Integer -> Integer -> Integer -> Integer -> Bool
    prob4_property2 v w x y z = [v, (w + w), x, (y + y), z] == prob4 [v,w,x,y,z]
prob5_test1 = quickCheckWith (stdArgs {maxSuccess = 1000}) prob5_property
  where
    prob5_property :: [Integer] -> Bool
    prob5_property xs = prob5 (map (abs) xs) == go' xs
    go' :: [Integer] -> Integer 
    go' is = go1 (map (abs) is) 0
      where go1 :: [Integer] -> Integer -> Integer
            go1 [] n     = n
            go1 (x:xs) n | (x < 10)   = go1 xs (x + n)
                         | (x > 9)    = go1 xs ((sum (go2 x)) + n)
                         | otherwise  = go1 xs n 
            go2 :: Integer -> [Integer]
            go2 x
              | x < 0      = []
              | otherwise  = map go3 $ show x
            go3 :: Char -> Integer
            go3 '0' = 0
            go3 '1' = 1
            go3 '2' = 2
            go3 '3' = 3
            go3 '4' = 4
            go3 '5' = 5
            go3 '6' = 6
            go3 '7' = 7
            go3 '8' = 8
            go3 '9' = 9

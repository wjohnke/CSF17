

push :: a -> [a] -> [a]
push x stack = x:stack

pop :: [a] -> (a, [a])
pop [] = error "Cannot pop an empty stack"
pop [x] = (x, [])
pop (x:xs) = (x,xs)

top :: [a] -> a
top [] = error "Cannot pop an empty stack."
top (x:xs) = x



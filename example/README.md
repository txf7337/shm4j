#example

##SharedBuffer.class
there have some properties at the end of shared memory

readIndexOffset、writeIndexOffset、flag

readIndexOffset save the readIndex
writeIndexOffset save the writeIndex

if writeIndex < readIndex
    flag = 1
else
    flag = 0

readableSize <= writeIndex + MAX_SIZE * flag - readIndex
writableSize <= readIndex + MAX_SIZE * (flag ^ 1) - writeIndex


##Read.class

# shm4j
share data by shared memory between processes(c,c++,java,python...)

## why create project
the shared memory is fastest ,because there is no socket,no io

## env
need GLIBC_2.14 by jna(4.x)

## how to use
see [example](https://github.com/txf7337/shm4j/blob/master/example/README.md)

## SharedMemory.class
it can only readable.can not create shared memory
if shared memory create by itself.will free shared memory when system exit

## alarm
if never used after init, remember to close it.

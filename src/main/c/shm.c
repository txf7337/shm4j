#include<stdio.h>
#include<sys/ipc.h>
#include<sys/types.h>
#include<sys/shm.h>
int g(int key, int size, int shmflg)
{
        return shmget(key, size, shmflg);
}

void *a(int shmid, const void *shmaddr, int shmflg)
{
        return shmat(shmid, shmaddr , shmflg);
}

int d(const void *shmaddr)
{
        return shmdt(shmaddr);
}

int c(int shmid, int cmd)
{
        return shmctl(shmid, cmd, NULL);
}
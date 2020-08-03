import java.util.Scanner;
import java.lang.Math;
import java.util.concurrent.Semaphore;


public class D3 extends Thread {
	private static volatile Semaphore assisting = new Semaphore(1, true);
	private static volatile Semaphore checking = new Semaphore(1, true);
	private static volatile Semaphore chairs = new Semaphore(1,true);
	public static volatile int numOfChair;
	public static D3 thread[];
	private int num;
	
	
	public D3(int num) {
		this.num = num;
	}
	
	public void run() {
		if(num == 0) {
			TA();
		}else {
				Student();
		}
	}
	
	private void TA() {
		while(true) {
			System.out.println("TA goes to check if anyone is waiting to be helped");
			if(chairs.availablePermits() != 0) {
			System.out.println("TA sees that there are no students waiting to be helped so goes to sleep");
			try {
				synchronized(this) {
				this.wait();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("TA hears a knock and wakes up");
			}else {
				System.out.println("TA sees that there is at least one student waiting to be helped so goes to help");
				try {
					chairs.release();
					Thread.sleep((int) (Math.random()*10000) + 1);
					synchronized(this) {
						this.notify();
					}
					synchronized(this) {
						this.wait();
						}
				} catch (InterruptedException e) {
						e.printStackTrace();
				}
				
			}
		}
	}
	
	private void Student() {
		while(true) {
			System.out.println("Student "+num+" is currently Coding");
			try {
				Thread.sleep((int) (Math.random()*50000) + 1);
			} catch (InterruptedException e) {
					e.printStackTrace();
			}
			System.out.println("Student "+num+" needs help and will go to TA");
			try {
				checking.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(chairs.availablePermits() == 1) {
				System.out.println("Student "+num+" sees that no chairs are taken and will go check if the TA is busy");
				if(assisting.availablePermits() != 0) {
					System.out.println("Student "+num+" sees that the TA is sleeping and knocks on the door");
					synchronized(thread[0]) {
					thread[0].notify();
					}
					acquireChair();
				}else {
					System.out.println("Student "+num+" sees that the TA is helping somebody and goes to take a seat");
					acquireChair();
				}
			}else {
				if(chairs.getQueueLength() < numOfChair) {
					//getqueuelength + chair semaphore (which is 1) = number of chairs taken
					System.out.println("Student "+num+" sees that there are "+(numOfChair-chairs.getQueueLength()-1)+" chairs available and goes to sit");
					acquireChair();
				}else {
					System.out.println("Student "+num+" sees that no chairs are available and goes back to Coding");
				}
			}
		
		}
	}
	
	private void acquireChair() {
		checking.release();
		try {
			chairs.acquire();
			assisting.acquire();
			synchronized(thread[0]) {
				thread[0].wait();
			}
			System.out.println("Student "+num+" received help and is going back to code");
			assisting.release();
			synchronized(thread[0]) {
				thread[0].notify();
				}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	
	public static void main(String args[]){	
		Scanner scanner = new Scanner(System.in);
		System.out.println("Insert Number of Students: ");
		int num = scanner.nextInt();
		thread = new D3[num+1];
		System.out.println("Insert Number of Chairs: ");
		numOfChair = scanner.nextInt();
		scanner.close();
		for(int i  = 0; i <= num; i++) {
		thread[i] = new D3(i);
		thread[i].start();  
		}
	}  
	
}

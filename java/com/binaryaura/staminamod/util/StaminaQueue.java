package com.binaryaura.staminamod.util;

import net.minecraft.util.MathHelper;

@Deprecated
public class StaminaQueue {
	
	public StaminaQueue(){
		queue = new int[0][2];
		queue2 = new float[0][2];
	}
	
	public void add (int amount){
		add(amount, amount);
	}
	
	public void add(int amount, double time){
		float delta = (float) (amount/(time * 40));
		add(amount, delta);
	}
	
	public void add(int amount, float delta){
		int change = MathHelper.floor_float(delta);
		delta -= change;
		
		int[][] temp = queue;
		float[][] temp2 = queue2;
		queue = new int[queue.length + 1][2];
		queue2 = new float[queue.length + 1][2];
		System.arraycopy(temp, 0, queue, 0, temp.length);
		System.arraycopy(temp2, 0, queue2, 0, temp2.length);
		queue[queue.length - 1][0] = change;
		queue[queue.length - 1][1] = amount;
		queue2[queue2.length - 1][0] = 0;
		queue2[queue2.length - 1][1] = delta;
		this.netChange += delta;
	}
	
	public void remove(int index){
		int[][] temp = queue;
		float[][] temp2 = queue2;
		queue = new int[queue.length - 1][2];
		queue2 = new float[queue2.length - 1][2];
		if(index != 0){
			System.arraycopy(temp, 0, queue, 0, index);
			System.arraycopy(temp2, 0, queue, 0, index);
		}
		System.arraycopy(temp, index + 1, queue, index, queue.length - index);
		System.arraycopy(temp2, index + 1, queue2, index, queue2.length - index);
		this.netChange -= temp[index][0];
	}
	
	public void update(){
		this.sumNetChange = 0;
		for(int i = 0; i < queue.length; i++){
			queue[i][1] -= queue[i][0];
			if(queue2[i][0] >= 1){
				int temp = MathHelper.floor_float(queue2[i][0]);
				queue[i][1] -= temp;
				queue2[i][0] -= temp;
				this.sumNetChange += temp;
			}
			if(queue[i][1] <= 0){
				this.sumNetChange += (int)(Math.round(queue2[i][0]));
				remove(i);
			}
		}
	}
	
	public int getNetChange(){
		return this.netChange + this.sumNetChange;
	}
	
	private int netChange = 0;
	private int sumNetChange = 0;
	private int[][] queue;
	private float[][] queue2;
}

package me.kalmemarq.minicraft.bso;

public interface BsoTag {
	 int getId();
	 default int getAdditionalData() {
		 return 0;
	 }
}

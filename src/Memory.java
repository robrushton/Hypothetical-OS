public class Memory {
    
    public block[] memoryArray = new block[10];
    private final int[] memSize = {32,48, 24, 16, 64, 48, 32, 64, 48, 32}; //the given memory sizes
    
    public Memory() {
        for (int i = 0; i < memoryArray.length; i++) {
            memoryArray[i] = new block(i, memSize[i]);//fills the memory(array) with block objects
        }
    }
    
    public int getWastedSpace() {
        //compute and return wasted space
        int ws = 0;
        for (int i = 0; i < 10; i++) {
            if (memoryArray[i].getJobHere() != null) {
                ws += memoryArray[i].getSize() - memoryArray[i].getJobHere().getMemoryRequest();
            }
            else {
                ws += memoryArray[i].getSize();
            }
        }
        
        return ws;
    }

    
    public class block {
        //represents each block of memory
        private int segmentNumber;
        private int size;
        private boolean inUse;
        private Job jobHere;
        
        public block(int segmentNumber, int size) {
            this.segmentNumber = segmentNumber;
            this.size = size;
            jobHere = null;
            inUse = false;
        }

        public Job getJobHere() {
            return jobHere;
        }
        
        public void setJobHere(Job jobHere) {
            this.jobHere = jobHere;
        }
        
        public int getSegmentNumber() {
            return segmentNumber;
        }

        public void setSegmentNumber(int segmentNumber) {
            this.segmentNumber = segmentNumber;
        }
        
        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }
        
        public boolean isInUse() {
            return inUse;
        }

        public void setInUse(boolean inUse) {
            this.inUse = inUse;
        }
    }
}
public class Job {
    //hold job info that is generated
    public enum JobStatus {WAITING, READY, RUNNING, FINISHED;}
    private int ID;
    private int memoryRequest;
    private int timeRequest;
    private int memoryAssigned;
    private int timeRemaining;
    private JobStatus status;
    
    public Job(int ID, int memoryRequest, int timeRequest, JobStatus status) {
        this.ID = ID;
        this.memoryRequest = memoryRequest;
        this.timeRequest = timeRequest;
        this.status = status;
        this.timeRemaining = timeRequest;
        this.memoryAssigned = -1;
    }
    
    public Job(int ID, int memoryRequest, int timeRequest, int memoryAssigned, int timeRemaining, JobStatus status) {
        this.ID = ID;
        this.memoryRequest = memoryRequest;
        this.timeRequest = timeRequest;
        this.memoryAssigned = memoryAssigned;
        this.timeRemaining = timeRemaining;
        this.status = status;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getMemoryRequest() {
        return memoryRequest;
    }

    public void setMemoryRequest(int memoryRequest) {
        this.memoryRequest = memoryRequest;
    }

    public int getTimeRequest() {
        return timeRequest;
    }

    public void setTimeRequest(int timeRequest) {
        this.timeRequest = timeRequest;
    }

    public int getMemoryAssigned() {
        return memoryAssigned;
    }

    public void setMemoryAssigned(int memoryAssigned) {
        this.memoryAssigned = memoryAssigned;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }
}
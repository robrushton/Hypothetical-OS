public class Core {
    
    private Job currentJob;
    
    public Core() {
    }
    
    public void clear() {
        //clears the job out of the core
        if (currentJob != null && currentJob.getStatus() == Job.JobStatus.RUNNING) {
            currentJob.setStatus(Job.JobStatus.READY);
        }
        currentJob = null;
    }
    
    public void tick() {
        //decrements the time remaining
        if (currentJob != null) {
            currentJob.setTimeRemaining(currentJob.getTimeRemaining() - 1);//remove 1 from timeremaining
            if (currentJob.getTimeRemaining() == 0) {
                currentJob.setStatus(Job.JobStatus.FINISHED);
            }
        }
    }
    
    public void setCurrentJob(Job currentJob) {
        this.currentJob = currentJob;
    }
    public Job getCurrentJob() {
        return currentJob;
    }
}
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainOS {
    
    public static void main(String[] args) {
        Job[] jobList = generateJobs(20);
        //all 3 cases
        case1(jobList);
        resetJobs(jobList);
        case2(jobList);
        resetJobs(jobList);
        case3(jobList);
    }
    
    public static void resetJobs(Job[] jobs) {
        //resets time of the jobs
        for (int i = 0; i < 20; i++) {
            jobs[i].setMemoryAssigned(-1);
            jobs[i].setTimeRemaining(jobs[i].getTimeRequest());
            jobs[i].setStatus(Job.JobStatus.WAITING);
        }
    }
    
    private static int completedJobs(Job[] jobs) {
        //give number of competed jobs
        int cj = 0;
        for (int i = 0; i < 20; i++) {
            if (jobs[i].getStatus() == Job.JobStatus.FINISHED) {
                cj += 1;
            }
        }
        return cj;
    }
    
    private static boolean checkJobAlreadyInCore(Core[] cores, Job jobHere) {
        for (Core c : cores) {
            if (c.getCurrentJob() != null && c.getCurrentJob().getID() == jobHere.getID()) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean checkIfAllJobsFinished(Job[] jobs) {
        for (int i = 0; i < 20; i++) {
            if (jobs[i].getStatus() != Job.JobStatus.FINISHED) {
                return false;
            }
        }
        return true;
    }
    
    private static void clearFinishedJobs(Memory mem) {
        for (int i = 0; i < 10; i++) {
            if (mem.memoryArray[i].getJobHere() != null && mem.memoryArray[i].getJobHere().getStatus() == Job.JobStatus.FINISHED) {
                mem.memoryArray[i].setJobHere(null);
                mem.memoryArray[i].setInUse(false);
            }
        }
    }
    
    private static void firstFit(List<Job> jobList, Memory mem) {
        for (int i = 0; i < 20; i++) {
            if (jobList.get(i).getStatus() == Job.JobStatus.WAITING) {
                for (int j = 0; j < 10; j++) {
                    if (jobList.get(i).getMemoryRequest() <= mem.memoryArray[j].getSize() && !mem.memoryArray[j].isInUse()) {
                        jobList.get(i).setMemoryAssigned(mem.memoryArray[j].getSegmentNumber());
                        jobList.get(i).setStatus(Job.JobStatus.READY);
                        mem.memoryArray[j].setJobHere(jobList.get(i));
                        mem.memoryArray[j].setInUse(true);
                        j = 100;
                    }
                }
                if (jobList.get(i).getMemoryAssigned() == -1) {
                    i = 100;
                }
            }
        }
    }
      
    private static void bestFit(List<Job> jobList, Memory mem) {
        int bestSeg = -1;
        int bestDiff = 65;
        for (int i = 0; i < 20; i++) {
            if (jobList.get(i).getStatus() == Job.JobStatus.WAITING) {
                for (int j = 0; j < 10; j++) {
                    int q = mem.memoryArray[j].getSize() - jobList.get(i).getMemoryRequest();
                    if (q >= 0 && q < bestDiff && !mem.memoryArray[j].isInUse()) {
                        bestDiff = q;
                        bestSeg = mem.memoryArray[j].getSegmentNumber();
                    }
                }
                if (bestSeg > -1) {
                    jobList.get(i).setMemoryAssigned(bestSeg);
                    jobList.get(i).setStatus(Job.JobStatus.READY);
                    mem.memoryArray[bestSeg].setJobHere(jobList.get(i));
                    mem.memoryArray[bestSeg].setInUse(true);
                    bestSeg = -1;
                    bestDiff = 65;
                } else {
                    i = 100;
                }
            }
        }
    }
    
    private static void case1(Job[] jobs) {
        //Order by First-Come First-Served
        List<Job> jobList = Arrays.asList(jobs);//Create List for jobs in first-come order
        Memory mem = new Memory();
        int caseNum = 1;
        File allTicks = new File("case" + caseNum + "results.txt");
        try {
            PrintWriter pt = new PrintWriter(new FileWriter(allTicks, true));
            pt.println("T = Tick, I = ID, M = Memory Slot, T = Time Remaining, ST = Status, MW = Memory Wasted");
            pt.println();
            pt.println("T    I    M    T    ST         MW");
            pt.println("-----------------------------------");
            pt.close();
        } catch (IOException ex) {
            Logger.getLogger(MainOS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Creating 4 cores to do processes
        Core[] cores = new Core[4];
        for (int i = 0; i < cores.length; i++) {
            cores[i] = new Core();
        }
        
        
        //Simulation runs for 30 time units minus the first one above
        int lastSegmentUsed = 0;
        for (int i = 0; i < 30; i++) {
            //Adding Jobs to memory using first fit
            firstFit(jobList, mem);
            
            //Loop through cores
            for (int k = 0; k < 4; k++) {
                //Clear Core of job
                cores[k].clear();
            }
            
            
            //Load Jobs into Cores
            int count = 0;
            int coreUsing = 0;
            while (count < 4 && !checkIfAllJobsFinished(jobs)) {
                if (mem.memoryArray[lastSegmentUsed].isInUse()) {
                    if (!checkJobAlreadyInCore(cores, mem.memoryArray[lastSegmentUsed].getJobHere())) {
                        cores[coreUsing].setCurrentJob(mem.memoryArray[lastSegmentUsed].getJobHere());
                        mem.memoryArray[lastSegmentUsed].getJobHere().setStatus(Job.JobStatus.RUNNING);
                        if (coreUsing >= 3) {
                            coreUsing = 0;
                        } else {
                            coreUsing++;
                        }
                        count++;
                    } else {
                        count = 100;
                    }
                }
                if (lastSegmentUsed < 9) {
                    lastSegmentUsed++;
                } else {
                    lastSegmentUsed = 0;
                }
                //printJobs(jobs);
            }
            
            //Loop through cores
            for (int k = 0; k < 4; k++) {
                //Tick Core
                cores[k].tick();
            }          
            
            //Clear jobs that are finished
            clearFinishedJobs(mem);
            printAllJobs(jobs, caseNum, i, mem);
            try {
                PrintWriter pt = new PrintWriter(new FileWriter(allTicks, true));
                pt.println("-----------------------------------");
                pt.close();
            } catch (IOException ex) {
                Logger.getLogger(MainOS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Jobs Completed: " + completedJobs(jobs));
        try {
            PrintWriter pt = new PrintWriter(new FileWriter(allTicks, true));
            pt.println("Jobs Completed: " + completedJobs(jobs));
            pt.close();
        } catch (IOException ex) {
            Logger.getLogger(MainOS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void case2(Job[] jobs) {
        //Order by First-Come First-Served
        List<Job> jobList = Arrays.asList(jobs);//Create List for jobs in first-come order
        Memory mem = new Memory();
        int caseNum = 2;
        File allTicks = new File("case" + caseNum + "results.txt");
        try {
            PrintWriter pt = new PrintWriter(new FileWriter(allTicks, true));
            pt.println("T = Tick, I = ID, M = Memory Slot, T = Time Remaining, ST = Status");
            pt.println();
            pt.println("T    I    M    T    ST         MW");
            pt.println("-----------------------------------");
            pt.close();
        } catch (IOException ex) {
            Logger.getLogger(MainOS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Creating 4 cores to do processes
        Core[] cores = new Core[4];
        for (int i = 0; i < cores.length; i++) {
            cores[i] = new Core();
        }
        
        
        //Simulation runs for 30 time units minus the first one above
        int lastSegmentUsed = 0;
        for (int i = 0; i < 30; i++) {
            //Adding Jobs to memory using first fit
            bestFit(jobList, mem);
            
            //Loop through cores
            for (int k = 0; k < 4; k++) {
                //Clear Core of job
                cores[k].clear();
            }
            
            
            //Load Jobs into Cores
            int count = 0;
            int coreUsing = 0;
            while (count < 4 && !checkIfAllJobsFinished(jobs)) {
                if (mem.memoryArray[lastSegmentUsed].isInUse()) {
                    if (!checkJobAlreadyInCore(cores, mem.memoryArray[lastSegmentUsed].getJobHere())) {
                        cores[coreUsing].setCurrentJob(mem.memoryArray[lastSegmentUsed].getJobHere());
                        mem.memoryArray[lastSegmentUsed].getJobHere().setStatus(Job.JobStatus.RUNNING);
                        if (coreUsing >= 3) {
                            coreUsing = 0;
                        } else {
                            coreUsing++;
                        }
                        count++;
                    } else {
                        count = 100;
                    }
                }
                if (lastSegmentUsed < 9) {
                    lastSegmentUsed++;
                } else {
                    lastSegmentUsed = 0;
                }
            }
            
            //Loop through cores
            for (int k = 0; k < 4; k++) {
                //Tick Core
                cores[k].tick();
            }
            
            //printJobs(jobs);
            
            //Clear jobs that are finished
            clearFinishedJobs(mem);
            printAllJobs(jobs, caseNum, i, mem);
            try {
                PrintWriter pt = new PrintWriter(new FileWriter(allTicks, true));
                pt.println("-----------------------------------");
                pt.close();
            } catch (IOException ex) {
                Logger.getLogger(MainOS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Jobs Completed: " + completedJobs(jobs));
        try {
            PrintWriter pt = new PrintWriter(new FileWriter(allTicks, true));
            pt.println("Jobs Completed: " + completedJobs(jobs));
            pt.close();
        } catch (IOException ex) {
            Logger.getLogger(MainOS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void case3(Job[] jobs) {
        //Order by Shortest-Job First-Served
        List<Job> jobList = shortestBurstSort(jobs);
        Memory mem = new Memory();
        int caseNum = 3;
        File allTicks = new File("case" + caseNum + "results.txt");
        try {
            PrintWriter pt = new PrintWriter(new FileWriter(allTicks, true));
            pt.println("T = Tick, I = ID, M = Memory Slot, T = Time Remaining, ST = Status");
            pt.println();
            pt.println("T    I    M    T    ST         MW");
            pt.println("-----------------------------------");
            pt.close();
        } catch (IOException ex) {
            Logger.getLogger(MainOS.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Creating 4 cores to do processes
        Core[] cores = new Core[4];
        for (int i = 0; i < cores.length; i++) {
            cores[i] = new Core();
        }
        
        
        //Simulation runs for 30 time units minus the first one above
        int lastSegmentUsed = 0;
        for (int i = 0; i < 30; i++) {
            //Adding Jobs to memory using best fit
            bestFit(jobList, mem);
            
            //Loop through cores
            for (int k = 0; k < 4; k++) {
                //Clear Core of job
                cores[k].clear();
            }
            
            
            //Load Jobs into Cores
            int count = 0;
            int coreUsing = 0;
            while (count < 4 && !checkIfAllJobsFinished(jobs)) {
                if (mem.memoryArray[lastSegmentUsed].isInUse()) {
                    if (!checkJobAlreadyInCore(cores, mem.memoryArray[lastSegmentUsed].getJobHere())) {
                        cores[coreUsing].setCurrentJob(mem.memoryArray[lastSegmentUsed].getJobHere());
                        mem.memoryArray[lastSegmentUsed].getJobHere().setStatus(Job.JobStatus.RUNNING);
                        if (coreUsing >= 3) {
                            coreUsing = 0;
                        } else {
                            coreUsing++;
                        }
                        count++;
                    } else {
                        count = 100;
                    }
                }
                if (lastSegmentUsed < 9) {
                    lastSegmentUsed++;
                } else {
                    lastSegmentUsed = 0;
                }
            }
            
            //Loop through cores
            for (int k = 0; k < 4; k++) {
                //Tick Core
                cores[k].tick();
            }
            
            //printJobs(jobs);
            
            //Clear jobs that are finished
            clearFinishedJobs(mem);
            printAllJobs(jobs, caseNum, i, mem);
            try {
                PrintWriter pt = new PrintWriter(new FileWriter(allTicks, true));
                pt.println("----------------------------");
                pt.close();
            } catch (IOException ex) {
                Logger.getLogger(MainOS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("Jobs Completed: " + completedJobs(jobs));
        try {
            PrintWriter pt = new PrintWriter(new FileWriter(allTicks, true));
            pt.println("Jobs Completed: " + completedJobs(jobs));
            pt.close();
        } catch (IOException ex) {
            Logger.getLogger(MainOS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static List<Job> shortestBurstSort(Job[] jobs) {
        List<Job> jobList = new LinkedList<>();
        ArrayList<Job> tempJobs = new ArrayList<>(Arrays.asList(jobs));//Temp arraylist to help add to job queue
        while(tempJobs.size() > 0) {//until array list empty
            int count = 0;//keep up with to tell the shortest
            for (int i = 0; i < tempJobs.size(); i ++) {//find shortest and its at count
                if (tempJobs.get(i).getTimeRequest() < tempJobs.get(count).getTimeRequest()) {
                    count = i;
                }
            }
            jobList.add(tempJobs.get(count));//add shortest to queue
            tempJobs.remove(count);//remove shortest from temp arraylist
        }
        return jobList;
    }
    
    private static Job[] generateJobs(int size) {
        Job[] jobList = new Job[size];
        for (int i = 0; i < size; i ++) {
            Random r = new Random();
            int mbits = r.nextInt(49) + 16;
            int burst = r.nextInt(9) + 2;
            
            jobList[i] = new Job(i, mbits, burst, Job.JobStatus.WAITING);
        }
        return jobList;
    }
    
    public static void printJobs(List<Job> jobList) {
        for (Job j : jobList) {
            System.out.println(j.getID());
            System.out.println(j.getMemoryRequest());
            System.out.println(j.getMemoryAssigned());
            System.out.println(j.getTimeRequest());
            System.out.println(j.getTimeRemaining());
            System.out.println(j.getStatus());
        }
    }

    public static void printAllJobs(Job[] jobList, int caseNum, int tick, Memory mem) {
        File allTicks = new File("case" + caseNum + "results.txt");
        for (int i = 0; i < 20; i ++) {
            String space1;
            String space2;
            String space3;
            String space4;
            String space5;
            if (tick > 9) {
                space1 = "   ";
            }
            else {
                space1 = "    ";
            }
            if (jobList[i].getID() > 9) {
                space2 = "   ";
            }
            else {
                space2 = "    ";
            }
            if (jobList[i].getMemoryAssigned() == -1) {
                space3 = "   ";
            }
            else {
                space3 = "    ";
            }
            if (jobList[i].getTimeRemaining() == 10) {
                space4 = "   ";
            }
            else {
                space4 = "    ";
            }
            if (jobList[i].getStatus() == Job.JobStatus.WAITING || jobList[i].getStatus() == Job.JobStatus.RUNNING) {
                space5 = "    ";
            }
            else if (jobList[i].getStatus() == Job.JobStatus.READY) {
                space5 = "      ";
            }
            else {
                space5 = "   ";
            }
            System.out.println(tick + space1 + jobList[i].getID() + space2
                + jobList[i].getMemoryAssigned() + space3 + 
                jobList[i].getTimeRemaining() + space4 + jobList[i].getStatus() + space5 + mem.getWastedSpace());
            
            try (PrintWriter pt = new PrintWriter(new FileWriter(allTicks, true))) {
                pt.println(tick + space1 + jobList[i].getID() + space2
                + jobList[i].getMemoryAssigned() + space3 + 
                jobList[i].getTimeRemaining() + space4 + jobList[i].getStatus() + space5 + mem.getWastedSpace());
                pt.close();
            }
            catch (FileNotFoundException ex) {
                Logger.getLogger(MainOS.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainOS.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        System.out.println("----------------------------------");
    }
}
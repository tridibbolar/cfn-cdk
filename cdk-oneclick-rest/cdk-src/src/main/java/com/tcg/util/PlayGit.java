package com.tcg.util;

import java.io.File;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

public class PlayGit {
    public static void main(String[] args) {
        // Create a new repository
        //Repository localRepo = FileRepositoryBuilder.create(new File("/tmp/new_repo/.git"));
       
        //System.out.println(localRepo.getDirectory().getAbsolutePath());
        //Git git = new Git(localRepo);
        //localRepo.create();
        //git.add().addFilepattern(".").call();
        //git.commit().setMessage("test message").call();
        //git.close();
    }
}
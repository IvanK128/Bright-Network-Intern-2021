package com.google;

import java.util.*;

public class VideoPlayer {

    private final VideoLibrary videoLibrary;
    private boolean videoPlaying = false;
    private Video playingVideo;
    private Video stoppingVideo;
    private boolean videoPaused = false;
    List<String> playlistNames = new ArrayList<>();
    List<Video> videoInPlaylist = new ArrayList<>();
    HashMap<String, List<Video>> videoHashMap = new HashMap<>();
    private int numberOfValidChoices;
    List<Video> matchVideo = new ArrayList<>();
    List<Video> flaggedVideo = new ArrayList<>();
    HashMap<Video, String> flaggedVideoHashMap = new HashMap<>();

    public VideoPlayer() {
        this.videoLibrary = new VideoLibrary();
    }

    public void numberOfVideos() {
        System.out.printf("%s videos in the library%n", videoLibrary.getVideos().size());
    }

    public void showAllVideos() {
        System.out.println("Here's a list of all available videos:");
        List<Video> videoList = new ArrayList<>(videoLibrary.getVideos());
        //sort by title
        Collections.sort(videoList, Comparator.comparing(Video::getTitle));

        for (Video video : videoList) {
            boolean flagged = checkFlaggedVideo(video);
            if (flagged == true) {
                String reason = getReason(video);
                System.out.println(video + " - FLAGGED (reason: " + reason + ")");
            } else {
                System.out.println(video);
            }
        }
    }

    public void playVideo(String videoId) {
        boolean videoExist = false;
        String reason;
        Video inputVideo = getVideo(videoId);
        boolean flaggedVideo = checkFlaggedVideo(inputVideo);
        List<Video> videoList = new ArrayList<>(videoLibrary.getVideos());
        for (Video v : videoList) {
            if (v.getVideoId().equals(videoId)) {
                videoExist = true;
                videoPaused = false;
                playingVideo = v;
            }
        }

        if (videoExist) {
// set videoPlaying to true after the first video played and save the current video to the stop video for later use
            if (!videoPlaying) {
                //Not Flagged
                if (flaggedVideo == false) {
                    System.out.println("Playing video: " + playingVideo.getTitle());
                    videoPlaying = true;
                    stoppingVideo = playingVideo;
                }
                //Flagged
                else {
                    reason = getReason(inputVideo);
                    System.out.println("Cannot play video: Video is currently flagged (reason: " + reason + ")");
                }
// stop video is the current playing video and the value only change when there is a new command to playVideo
            } else {
                //Not Flagged
                if (flaggedVideo == false) {
                    System.out.println("Stopping video: " + stoppingVideo.getTitle());
                    System.out.println("Playing video: " + playingVideo.getTitle());
                    stoppingVideo = playingVideo;
                    //Flagged
                } else {
                    reason = getReason(inputVideo);
                    System.out.println("Cannot play video: Video is currently flagged (reason: " + reason + ")");
                }
            }
        } else {
            System.out.println("Cannot play video: Video does not exist");
        }
    }

    public void stopVideo() {
//No Video Playing
        if (!videoPlaying) {
            System.out.println("Cannot stop video: No video is currently playing");
//There a video playing
        } else {
            System.out.println("Stopping video: " + playingVideo.getTitle());
            videoPlaying = false;
        }
    }

    public void playRandomVideo() {
        List<Video> videoList = new ArrayList<>(videoLibrary.getVideos());
        videoPaused = false;
        Random random = new Random();
        int i = random.nextInt(videoList.size());
        playingVideo = videoList.get(i);

//Check Flagged video
        boolean flagged = false;
        if (videoList.size() == flaggedVideo.size()) {
            flagged = true;
        }
//list of video equal to list of flagged video size mean there are no song left because there is no duplication
        if (flagged == true) {
            System.out.println("No videos available");
        } else {
//No Video Currently played
            if (videoPlaying == false) {
                System.out.println("Playing video: " + playingVideo.getTitle());
                videoPlaying = true;
                stoppingVideo = playingVideo;
//There is a video playing
            } else {
                System.out.println("Stopping video: " + stoppingVideo.getTitle());
                System.out.println("Playing video: " + playingVideo.getTitle());
                stoppingVideo = playingVideo;
            }
        }
    }

    public void pauseVideo() {
// if there is a video playing and not paused
        if (videoPlaying && videoPaused == false) {
            System.out.println("Pausing video: " + playingVideo.getTitle());
            videoPaused = true;
// Current Played Video already paused
        } else if (videoPlaying == true && videoPaused == true) {
            System.out.println("Video already paused: " + playingVideo.getTitle());
// No Video Played yet
        } else {
            System.out.println("Cannot pause video: No video is currently playing");
        }
    }

    public void continueVideo() {
//Current Video is playing and not paused
        if (videoPlaying == true && videoPaused == false) {
            System.out.println("Cannot continue video: Video is not paused");
// There a video been played but in pause mode
        } else if (videoPlaying == true && videoPaused == true) {
            System.out.println("Continuing video: " + playingVideo.getTitle());
// No Video is been played
        } else {
            System.out.println("Cannot continue video: No video is currently playing");
        }
    }

    public void showPlaying() {
// Playing Video
        if (videoPlaying == true) {
            String currentVideo = playingVideo.toString().strip();
            // Video is Not Paused
            if (!videoPaused) {
                System.out.println("Currently playing: " + currentVideo);
                // Video is paused
            } else {
                System.out.println("Currently playing: " + currentVideo + " - PAUSED");
            }
//No video is playing
        } else {
            System.out.println("No video is currently playing");
        }
    }

    public void createPlaylist(String playlistName) {
        boolean sameName;
//no playlist created yet
        if (playlistNames.isEmpty()) {
            System.out.println("Successfully created new playlist: " + playlistName);
            playlistNames.add(playlistName);
            videoHashMap.put(playlistName, videoInPlaylist);
// a playlist already exist
        } else {
            sameName = checkNameInPlaylist(playlistNames, playlistName);
            if (sameName == true) {
                System.out.println("Cannot create playlist: A playlist with the same name already exists");
            } else {
                System.out.println("Successfully created new playlist: " + playlistName);
                playlistNames.add(playlistName);
                videoHashMap.put(playlistName, videoInPlaylist);
            }
        }
    }

    public void addVideoToPlaylist(String playlistName, String videoId) {
        boolean doubleVideo;
        Video inputVideo = getVideo(videoId);
        boolean flagged = checkFlaggedVideo(inputVideo);

// Check Playlist Existence
        boolean playlistExist = checkNameInPlaylist(playlistNames, playlistName);

// Check Video Existence
        Video searchVideo = videoLibrary.getVideo(videoId);
        boolean videoExist = checkVideoExistence(searchVideo);

//Playlist Exist
        if (playlistExist == true) {
            // Video Exist
            if (videoExist == true) {
                //not flagged
                if (flagged == false) {
                    if (videoInPlaylist.isEmpty()) {
                        System.out.println("Added video to " + playlistName + ": " + searchVideo.getTitle());
                        videoInPlaylist.add(searchVideo);
                        videoHashMap.put(playlistName, videoInPlaylist);
                        //There is a video in the playlist
                    } else {
                        doubleVideo = checkVideoInPlaylist(videoInPlaylist, searchVideo);
                        //a same video exist in the playlist
                        if (doubleVideo == true) {
                            System.out.println("Cannot add video to " + playlistName + ": Video already added");
                        }
                        //different video
                        else {
                            System.out.println("Added video to " + playlistName + ": " + searchVideo.getTitle());
                            videoInPlaylist.add(searchVideo);
                            videoHashMap.put(playlistName, videoInPlaylist);
                        }
                    }
                    //flagged
                } else {
                    String reason = getReason(inputVideo);
                    System.out.println("Cannot add video to " + playlistName + ": Video is currently flagged (reason: " + reason + ")");
                }
                //Video do Not Exist
            } else {
                System.out.println("Cannot add video to " + playlistName + ": Video does not exist");
            }
//Playlist Do Not Exist
        } else {
            System.out.println("Cannot add video to " + playlistName + ": Playlist does not exist");
        }
    }

    public void showAllPlaylists() {
        if (playlistNames.isEmpty()) {
            System.out.println("No playlists exist yet");
        } else {
            Collections.sort(playlistNames);
            System.out.println("Showing all playlists: ");
            for (String name : playlistNames) {
                System.out.println(name);
            }
        }
    }

    public void showPlaylist(String playlistName) {
        boolean noVideo = false;
//Check Playlist Existence
        boolean playlistExist = checkNameInPlaylist(playlistNames, playlistName);
//Playlist Exist
        if (playlistExist == true) {
            System.out.println("Showing playlist: " + playlistName);
            //Check Number of Video
            if (videoInPlaylist.isEmpty()) {
                noVideo = true;
            }
            //No Video
            if (noVideo == true) {
                System.out.println("No videos here yet");
            }
            // There is a video
            else {
                for (Video video : videoInPlaylist) {
                    boolean flagged = checkFlaggedVideo(video);
                    //flagged
                    if (flagged == true) {
                        String reason = getReason(video);
                        System.out.println(video + " - FLAGGED (reason: " + reason + ")");
                    } else {
                        System.out.println(video);
                    }
                }
            }
        }
//Playlist do not exist
        else {
            System.out.println("Cannot show playlist " + playlistName + ": Playlist does not exist");
        }
    }

    public void removeFromPlaylist(String playlistName, String videoId) {
        boolean videoInsidePlaylist;

// Check Playlist Existence
        boolean playlistExist = checkNameInPlaylist(playlistNames, playlistName);

// Check Video Existence
        Video videoToRemove = videoLibrary.getVideo(videoId);
        boolean videoExist = checkVideoExistence(videoToRemove);

//Playlist Exist
        if (playlistExist == true) {
            if (videoExist == true) {
                //Video Exist, Then check is video in the playlist
                videoInsidePlaylist = checkVideoInPlaylist(videoInPlaylist, videoToRemove);
                //yes
                if (videoInsidePlaylist == true) {
                    System.out.println("Removed video from " + playlistName + ": " + videoToRemove.getTitle());
                    videoInPlaylist.remove(videoToRemove);
                }
                //No
                else {
                    System.out.println("Cannot remove video from " + playlistName + ": Video is not in playlist");
                }
            }
            //Video do not Exist
            else {
                System.out.println("Cannot remove video from " + playlistName + ": Video does not exist");
            }
//Playlist do not exist
        } else {
            System.out.println("Cannot remove video from " + playlistName + ": Playlist does not exist");
        }
    }

    public void clearPlaylist(String playlistName) {
//Get playlist name and Check Playlist Existence
        String actualName = getPlaylistName(playlistName);
        boolean playlistExist = checkNameInPlaylist(playlistNames, actualName);
        //Playlist Exist
        if (playlistExist) {
            System.out.println("Successfully removed all videos from " + playlistName);
            videoInPlaylist.clear();
        }
        //Playlist do not exist
        else {
            System.out.println("Cannot clear playlist " + playlistName + ": Playlist does not exist");
        }
    }

    public void deletePlaylist(String playlistName) {
        boolean sameName;
//check Playlist Existence
        sameName = checkNameInPlaylist(playlistNames, playlistName);
//Playlist exist
        if (sameName == true) {
            System.out.println("Deleted playlist: " + playlistName);
            videoHashMap.remove(playlistName);
        }
//Playlist does not exist
        else {
            System.out.println("Cannot delete playlist " + playlistName + ": Playlist does not exist");
        }
    }

    public void searchVideos(String searchTerm) {
        boolean validSearch = false;
        matchVideo = new ArrayList<>();

// Check search term and add match video
        for (Video v : videoLibrary.getVideos()) {
            String title = v.getTitle().toLowerCase();
            if (title.contains(searchTerm.toLowerCase())) {
                boolean flagged = checkFlaggedVideo(v);
                if (flagged == false) {
                    validSearch = true;
                    matchVideo.add(v);
                }
            }
        }
        searchingVideoWithSameCharacteristic(validSearch, searchTerm);
    }

    public void searchVideosWithTag(String videoTag) {
        boolean validSearch = false;
        matchVideo = new ArrayList<>();
        List<List<String>> originalTagList = new ArrayList<>();
        List<List<String>> matchedTagList = new ArrayList<>();

//Check validity and get Video list
        videoTag = videoTag.toLowerCase();
        //get list of tags
        for (Video v : videoLibrary.getVideos()) {
            originalTagList.add(v.getTags());
        }
        //get the video tag that has the same as searched video tag
        for (List<String> videoTags : originalTagList) {
            for (String vidTag : videoTags) {
                if (vidTag.contains(videoTag)) {
                    matchedTagList.add(videoTags);
                    validSearch = true;
                }
            }
            break;
        }

        if (validSearch == true) {
// get title of video where it is the same tag
            for (Video v : videoLibrary.getVideos()) {
                for (List<String> a : matchedTagList) {
                    if (v.getTags().equals(a)) {
                        boolean flagged = checkFlaggedVideo(v);
                        if (flagged == false) {
                            matchVideo.add(v);
                        }
                    }
                    break;
                }
            }
        }
        searchingVideoWithSameCharacteristic(validSearch, videoTag);
    }


    public void searchingVideoWithSameCharacteristic(boolean validSearch, String searchInput) {
//Valid search
        if (validSearch == true) {
            showSearchResult(searchInput);

            //ask question
            System.out.println("Would you like to play any of the above? If yes, specify the number of the video.");
            System.out.println("If your answer is not a valid number, we will assume it's a no.");

            //get user Input
            boolean validChoice = false;
            int choice = 0;
            var userInput = new Scanner(System.in);
            if (userInput.hasNextInt()) {
                choice = userInput.nextInt();
            }

            //check user choice and play video
            if (choice > 0) {
                if (choice <= numberOfValidChoices) {
                    validChoice = true;
                }
            }

            if (validChoice) {
                Video userPick = matchVideo.get(choice - 1);
                String videoId = userPick.getVideoId();
                playVideo(videoId);
            }
        }
//Invalid
        else {
            System.out.println("No search results for " + searchInput);
        }
    }

    public void showSearchResult(String input) {
        //sort video
        Collections.sort(matchVideo, Comparator.comparing(Video::getTitle));

        // print match video
        System.out.println("Here are the results for " + input + ": ");
        for (int i = 0; i < matchVideo.size(); i++) {
            System.out.println((i + 1) + ") " + matchVideo.get(i));
        }
        numberOfValidChoices = matchVideo.size();
    }

    public void flagVideo(String videoId) {
        Video inputVideo = videoLibrary.getVideo(videoId);
        String reason = "Not supplied";
// Check Video Existence
        boolean videoExist = checkVideoExistence(inputVideo);
// Check Video been flagged
        boolean videoFlagged = checkFlaggedVideo(inputVideo);

//Check is there a video playing now
        Video currentVideo = null;
        boolean playingVideoNow = videoPlaying;
        if (playingVideoNow == true) {
            currentVideo = playingVideo;
        }

//Exist
        if (videoExist == true) {
            //Not Flagged
            if (videoFlagged == false) {
                //Check is the video to flagged playing currently
                if (currentVideo != null && (inputVideo.equals(currentVideo))) {
                    System.out.println("Stopping video: " + playingVideo.getTitle());
                    videoPlaying = false;
                }
                System.out.println("Successfully flagged video: " + inputVideo.getTitle() + " (reason: Not supplied)");
                flaggedVideo.add(inputVideo);
                flaggedVideoHashMap.put(inputVideo, reason);
            }
            //Flagged
            else {
                System.out.println("Cannot flag video: Video is already flagged");
            }
        }
//Video Not Exist
        else {
            System.out.println("Cannot flag video: Video does not exist");
        }
    }

    public void flagVideo(String videoId, String reason) {
        Video inputVideo = videoLibrary.getVideo(videoId);
// Check Video Existence
        boolean videoExist = checkVideoExistence(inputVideo);
// Check Video been flagged
        boolean videoFlagged = checkFlaggedVideo(inputVideo);
//Check is there a video playing now
        Video currentVideo = null;
        boolean playingVideoNow = videoPlaying;
        if (playingVideoNow == true) {
            currentVideo = playingVideo;
        }

//Exist
        if (videoExist == true) {
            //Not Flagged
            if (videoFlagged == false) {
                //Check is the video to flagged playing currently
                if (currentVideo != null && (inputVideo.equals(currentVideo))) {
                    System.out.println("Stopping video: " + playingVideo.getTitle());
                    videoPlaying = false;
                }
                System.out.println("Successfully flagged video: " + inputVideo.getTitle() + " (reason: " + reason + ")");
                flaggedVideo.add(inputVideo);
                flaggedVideoHashMap.put(inputVideo, reason);
            }
            //Flagged
            else {
                System.out.println("Cannot flag video: Video is already flagged");
            }
//Video Not Exist
        } else {
            System.out.println("Cannot flag video: Video does not exist");
        }
    }

    public void allowVideo(String videoId) {
        Video inputVideo = videoLibrary.getVideo(videoId);
// Check Video Existence
        boolean videoExist = checkVideoExistence(inputVideo);
// Check Video been flagged
        boolean videoFlagged = checkFlaggedVideo(inputVideo);

//Video Exist
        if (videoExist == true) {
            //Video Flagged
            if (videoFlagged == true) {
                System.out.println("Successfully removed flag from video: " + inputVideo.getTitle());
                flaggedVideo.remove(inputVideo);
                flaggedVideoHashMap.remove(inputVideo);

            }
            //Video not Flagged
            else {
                System.out.println("Cannot remove flag from video: Video is not flagged");
            }
        }
//Video not Exist
        else {
            System.out.println("Cannot remove flag from video: Video does not exist");
        }
    }

    //check and get the actual name of the playlist
    public String getPlaylistName(String input) {
        String actualName = "";
        Set<String> nameInSet = videoHashMap.keySet();
        for (String n : nameInSet) {
            if (n.equalsIgnoreCase(input)) {
                actualName = n;
            }
        }
        return actualName;
    }

    //check duplicate name
    public boolean checkNameInPlaylist(List<String> listOfNames, String input) {
        boolean sameName = false;
        if (!listOfNames.isEmpty()) {
            Iterator<String> iterator = playlistNames.iterator();
            sameName = input.equalsIgnoreCase(iterator.next());
        }
        return sameName;
    }

    //check duplicate video
    public boolean checkVideoInPlaylist(List<Video> listOfVideo, Video input) {
        boolean videoExist = false;
        for (Video v : listOfVideo) {
            if (v.equals(input)) {
                videoExist = true;
                break;
            }
        }
        return videoExist;
    }

    //check video existence
    public boolean checkVideoExistence(Video video) {
        boolean videoExist = false;
        if (video != null) {
            videoExist = true;
        }
        return videoExist;
    }

    //check flagged video
    public boolean checkFlaggedVideo(Video video) {
        boolean flagged = false;
        if (!flaggedVideo.isEmpty()) {
            for (Video v : flaggedVideo) {
                if (v.equals(video)) {
                    flagged = true;
                    break;
                }
            }
        }
        return flagged;
    }

    //get reason of flagged video
    public String getReason(Video video) {
        String reason = flaggedVideoHashMap.get(video);
        return reason;
    }

    // get video using videoId
    public Video getVideo(String videoId) {
        Video inputVideo = videoLibrary.getVideo(videoId);
        return inputVideo;
    }

}
package utils.observer;

public enum EventType {

    /* Received from client*/

    HeartBeat,

    FileList,
    GetFile,
    RemoveFile,
    SaveFile,
    SignIn,
    SignUp,
    SignOut,

    /* Sent to client */
    FileListResult,
    GetFileResult,
    RemoveFileResult,
    SaveFileResult,
    SignResult,

    /* Received from sbe */
    SendResult,
    EraseResult,
    RetrieveResult,

    /* Sent to sbe */
    SendFile,
    EraseFile,
    RetrieveFile,

    /* Internal event type */
    SBEConnected,

}

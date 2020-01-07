package com.erman.drawerfm.fragmentsimport android.content.Contextimport android.content.IntentFilterimport android.os.Bundleimport android.os.Parcelableimport android.view.LayoutInflaterimport android.view.Viewimport android.view.ViewGroupimport android.widget.Toastimport androidx.core.view.isVisibleimport androidx.fragment.app.Fragmentimport androidx.localbroadcastmanager.content.LocalBroadcastManagerimport androidx.recyclerview.widget.LinearLayoutManagerimport com.erman.drawerfm.Rimport com.erman.drawerfm.adapters.DirectoryRecyclerViewAdapterimport com.erman.drawerfm.utilities.FileBroadcastReceiverimport com.erman.drawerfm.utilities.getFilesimport kotlinx.android.synthetic.main.fragment_file_list.*import java.io.Fileclass ListDirFragment : Fragment() {    private lateinit var directoryRecyclerViewAdapter: DirectoryRecyclerViewAdapter    private lateinit var path: String    private lateinit var onClickCallback: OnItemClickListener    private lateinit var fileBroadcastReceiver: FileBroadcastReceiver    private var fileList: List<Parcelable>? = null    interface OnItemClickListener {        fun onClick(directory: File)        fun onLongClick(directory: File)    }    companion object {        fun buildFragment(path: String): ListDirFragment {            val fragment = ListDirFragment()            val argumentBundle = Bundle()            argumentBundle.putString("path", path)            fragment.arguments = argumentBundle            return fragment        }    }    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        if (arguments?.getParcelableArrayList<Parcelable>("files") != null) //if we are searching for a file        {            fileList = arguments?.getParcelableArrayList<Parcelable>("files")            path = ""        } else {            path = arguments?.getString("path").toString()        }        fileBroadcastReceiver = FileBroadcastReceiver(path) { updateData() }    }    override fun onResume() {        super.onResume()        context?.let {            LocalBroadcastManager.getInstance(it).registerReceiver(                fileBroadcastReceiver,                IntentFilter(getString(R.string.file_broadcast_receiver))            )        }    }    override fun onPause() {        super.onPause()        context?.let {            LocalBroadcastManager.getInstance(it).unregisterReceiver(fileBroadcastReceiver)        }    }    override fun onAttach(context: Context) {        super.onAttach(context)        try {            onClickCallback = context as OnItemClickListener        } catch (e: Exception) {            throw Exception("${context} fragments.ListDirFragment.OnItemCLickListener is not implemented")        }    }    override fun onCreateView(        inflater: LayoutInflater,        container: ViewGroup?,        savedInstanceState: Bundle?    ): View? {        return inflater.inflate(R.layout.fragment_file_list, container, false)    }    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {        super.onViewCreated(view, savedInstanceState)        initViews()    }    private fun initViews() {        fileListRecyclerView.layoutManager = LinearLayoutManager(context)        directoryRecyclerViewAdapter = DirectoryRecyclerViewAdapter()        fileListRecyclerView.adapter = directoryRecyclerViewAdapter        directoryRecyclerViewAdapter.onClickListener = {            onClickCallback.onClick(it)        }        directoryRecyclerViewAdapter.onLongClickListener = {            onClickCallback.onLongClick(it)        }        updateData()    }    private fun updateData() {        try {            var files = getFiles(path)            if (files.isEmpty()) {                emptyFolderTextView.isVisible = true                emptyFolderTextView.text = getString(R.string.empty_folder)            } else                emptyFolderTextView.isVisible = false            directoryRecyclerViewAdapter.updateData(files)        } catch (err: IllegalStateException) {                Toast.makeText(context, getString(R.string.invalid_path), Toast.LENGTH_LONG).show()        }    }}
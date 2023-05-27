package com.example.myapplication

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.myapplication.classes.Note
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var editTextPriority: EditText
    private lateinit var saveButton: Button
    private lateinit var loadButton: Button
    private lateinit var textViewData: TextView


    //private lateinit var listener:ListenerRegistration  //allows us to remove a listener when we remove the app
    private val db: FirebaseFirestore =
        FirebaseFirestore.getInstance()  //getting an instance of the db
   // private val docRf: DocumentReference = db.collection("Notebook").document("My first note")
    private val noteBookRf: CollectionReference =
        db.collection("Notebook") //this allows us to add new notes to it and its refers to a collection of documents
    private var lastResult:DocumentSnapshot?=null  //a document snapshot and its nullable
    //a document reference in our db so we don't keep on typing this over and over again
    private val KEY_TITLE = "title"  //for mutable maps
    private val KEY_DESCRIPTION = "description" //same as above
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextTitle = findViewById(R.id.edit_text_title)
        editTextDescription = findViewById(R.id.edit_text_description)
        saveButton = findViewById(R.id.button_add_button)
        loadButton = findViewById(R.id.load_button)
        textViewData = findViewById(R.id.text_view_data)
        editTextPriority = findViewById(R.id.edit_text_priority)


        saveButton.setOnClickListener {
            addNote()
        }

        loadButton.setOnClickListener {
            loadNotes()
        }

    }




    private fun addNote() {
        val title = editTextTitle.text.toString()
        val description = editTextDescription.text.toString()
        /*  //THE FIRST METHOD MAP
            val note =
                mutableMapOf<String, Any>()  //in firebase data is stored in pairs so we need a mutable map
            note.put(KEY_TITLE, title)
            note.put(KEY_DESCRIPTION, description)

         */
        if (editTextPriority.text.toString().isEmpty()) {
            editTextPriority.setText("0")
        }
        val priority = editTextPriority.text.toString().toInt()
        val note = Note(title, description, priority)

        noteBookRf.add(note)  //adds a new document
            //or u can type docRf.set(note) since we made a reference
            //setting a collection name and a document name firebase can do it auto tho then setting it to the note map
            .addOnSuccessListener {
                Toast.makeText(this@MainActivity, "Note added!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this@MainActivity, "Error: note was not added!", Toast.LENGTH_SHORT)
                    .show()

            }

    }

    private fun loadNotes() {

        val query=if (lastResult==null) {
            noteBookRf.orderBy("priority").limit(3)
        }
        else {
             noteBookRf.orderBy("priority").startAfter(lastResult as DocumentSnapshot).limit(3)
        }

        query.get().addOnSuccessListener {
            var data=""
            for (queryDocumentSnapshot in it ) {

            val note:Note=queryDocumentSnapshot.toObject(Note::class.java)
                note.id=queryDocumentSnapshot.id

                val title=note.title
                val description=note.description
                val priority=note.priority
                val id=note.id

                data += "ID: $id "+ "Title: " + title + "\nDescription: $description" +
                        "\nPriority: $priority \n\n"
            }
            if (it.size()>0) {  //this if statement prevents app from crashing
                data += "-----------------\n\n"
                textViewData.append(data) //so it doesn't override the previous data
                lastResult = it.documents[it.size() - 1]
            }
        }
    }


        /*
    .addOnSuccessListener {  //we are basically looping through a query of document snapshots
            querydocumentsnapshots ->
        var data = " "  //used to append the data we get

        for (documentSnapshot in querydocumentsnapshots) {

            val note = documentSnapshot.toObject(Note::class.java)
            val title = note.title
            val description = note.description
            val priority = note.priority
            data += "Title: " + title + "\nDescription: $description" +
                    "\nPriority: $priority \n\n"
        }
        textViewData.text = data
    }.addOnFailureListener {
        Log.d(TAG,it.toString())  //the link to make the query online
    }

         */
    }





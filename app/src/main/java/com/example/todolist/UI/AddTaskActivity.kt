package com.example.todolist.UI

import android.app.Activity
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.todolist.datasource.TaskDataSource
import com.example.todolist.databinding.ActivityAddTaskBinding
import com.example.todolist.extensions.format
import com.example.todolist.extensions.text
import com.example.todolist.model.Task
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.sql.Date
import java.util.*


class AddTaskActivity : AppCompatActivity()
{
    private val builderDatePicker = MaterialDatePicker.Builder.datePicker()
    private lateinit var datePicker: MaterialDatePicker<Long>
    private lateinit var timePicker: MaterialTimePicker
    private lateinit var binding: ActivityAddTaskBinding

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?)
    {
        super.onCreate(savedInstanceState, persistentState)
        binding = ActivityAddTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.hasExtra(TASK_ID)){

            val taskId = intent.getIntExtra(TASK_ID, 0)
            TaskDataSource.findById(taskId)?. let {
                binding.tilTitle.text = it.title
                binding.tilDate.text = it.date
                binding.tilHour.text = it.hour
                intent.getIntExtra(TASK_ID, 0)
            }
        }

        insertListeners()
    }

    private fun insertListeners()
    {
         fun insertListeners() {
             binding.tilDate.editText?.setOnClickListener {

                 if (binding.tilDate.text != "") {
                     val date = SimpleDateFormat("dd/MM/yyyy").parse(binding.tilDate.text)

                     builderDatePicker.setSelection(date?.time)
                 }
                 datePicker = builderDatePicker.build()

                 datePicker.addOnPositiveButtonClickListener {
                     val timeZone = TimeZone.getDefault()
                     val offset = timeZone.getOffset(Date().time) * -1

                     binding.tilDate.text = Date(it + offset).format().toString()
                 }

                 datePicker.show(supportFragmentManager, "DATE_PICKER_TAG")
             }
         }

        binding.tilHour.editText?.setOnClickListener{
           val timePicker = MaterialTimePicker.Builder()
               .setTimeFormat(TimeFormat.CLOCK_24H).build()

            timePicker.addOnPositiveButtonClickListener{
               val minute = if(timePicker.minute in 0..9) "0${timePicker.minute}"
                else timePicker.minute

                val hour = if(timePicker.hour in 0..9) "0${timePicker.hour}"
                else timePicker.hour

            binding.tilHour.text= "$hour:$minute"
            }
            timePicker.show(supportFragmentManager, null)
        }

        binding.btnCancel.setOnClickListener {
        //botão de cancelar
            finish()
        }

        binding.btnNewTask.setOnClickListener {
            //botão de criar
            val task = Task(
                title = binding.tilTitle.text,
                date = binding.tilDate.text,
                hour = binding.tilHour.text

            )
            TaskDataSource.insertTask(task)

            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    companion object{
        const val TASK_ID = "task_id"
    }
}
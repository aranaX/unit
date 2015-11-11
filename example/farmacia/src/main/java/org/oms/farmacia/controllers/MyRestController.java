
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value="/users")
public class MyRestController {

    @RequestMapping(value="/{user}", method=RequestMethod.GET)
    public String getUser(@PathVariable Long user) {
	return "hola";
		    }

    @RequestMapping(value="/{user}/customers", method=RequestMethod.GET)
     List<String> getUserCustomers(@PathVariable Long user) {
	return new ArrayList<String>();
		    }

    @RequestMapping(value="/{user}", method=RequestMethod.DELETE)
    public boolean deleteUser(@PathVariable Long user) {
	return true;
		    }

}
